package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.RoomType
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createRoom(currentUser: User, chosenUser: User): Result<Room> {
        return try {
            val roomId = UserUtils.generateId(currentUser.uid, chosenUser.uid)
            val roomRef = firestore.collection("rooms").document(roomId)

            val snapshot = roomRef.get().await()
            if (snapshot.exists()) {
                val existing = snapshot.toObject(Room::class.java)
                return if (existing != null) Result.success(existing)
                else Result.failure(IllegalStateException("Room exists but parsing failed"))
            }

            val room = Room(
                participants = listOf(currentUser.uid, chosenUser.uid),
                createdAt = DateUtils.getCurrentTimestamp(),
                lastMessage = null,
                lastMessageSenderId = null,
                lastMessageTime = null,
                roomType = RoomType.PRIVATE,
                roomName = null,
                roomAvatar = null
            )
            roomRef.set(room).await()
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenUnreadTotal(userId: String) : Flow<Int> = callbackFlow {
        val reg = firestore.collection("rooms")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, err ->
                if (err != null) return@addSnapshotListener
                val total = snapshot?.documents?.sumOf { doc ->
                    val m = doc.get("unreadCounts") as? Map<*, *>
                    (m?.get(userId) as? Number ?: 0).toLong()
                } ?: 0L
                trySend(total.toInt())
            }
        awaitClose { reg.remove() }
    }

    fun listenUnreadByRoom(userId: String) : Flow<Map<String, Int>> = callbackFlow {
        val reg = firestore.collection("rooms")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, err ->
                if (err != null) return@addSnapshotListener
                val map = snapshot?.documents?.associate { doc ->
                    val counts = doc.get("unreadCounts") as? Map<*, *>
                    val count = (counts?.get(userId) as? Number ?: 0).toInt()
                    doc.id to count
                }.orEmpty()
                trySend(map)
            }
        awaitClose { reg.remove() }
    }
    suspend fun getRoom(currentUser: User, chosenUser: User) : Result<Room>{
        return try {
            val roomId = UserUtils.generateId(currentUser.uid, chosenUser.uid)
            val docRefs = firestore.collection("rooms")
            val snapshot = docRefs.document(roomId).get().await()
            if(!snapshot.exists()){
                Log.w("MyLog - UserRepo", "Document not found for roomId: $roomId")
                return Result.failure(Exception("Room not found for ID: $roomId"))
            }
            val room = snapshot.toObject(Room::class.java)
            if (room == null){
                return Result.failure(Exception("Room fetched null"))
            }
            Result.success(room)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateRoom(room: Room, message: Message) : Result<Room>{
        return try {
            val roomId = room.participants.sorted().joinToString("_")
            val updateRoom = room.copy(lastMessage = message.content, lastMessageSenderId = message.senderId, lastMessageTime = message.timestamp)
            firestore.collection("rooms").document(roomId).set(updateRoom).await()
            Result.success(updateRoom)
        } catch (e: Exception){
            Result.failure(e)
        }
    }


    fun listenRoomFlow(currentUser: User) : Flow<List<Room>> = callbackFlow{
        var roomList : List<Room> = emptyList()
        val roomReg = firestore.collection("rooms")
            .whereArrayContains("participants", currentUser.uid)
            .addSnapshotListener{ snapshot, error ->
            if(error != null){
                Log.d("MyLog - ChatRepo", "Error: $error")
                return@addSnapshotListener
            }

            roomList = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Room::class.java)
            } ?: emptyList()

            trySend(roomList)
        }

        awaitClose {
            roomReg.remove()
        }
    }

    suspend fun sendMessage(currentUser: User, chosenUserId: String, content: String) : Result<Unit>{
        if(content.isBlank()) return Result.success(Unit)
        return try {
            val roomId = UserUtils.generateId(currentUser.uid, chosenUserId)
            val docRefs = firestore
                .collection("rooms")
                .document(roomId)
                .collection("messages").document()
            val message = Message(
                uid = docRefs.id,
                roomId = roomId,
                senderId = currentUser.uid,
                senderName = currentUser.displayName ?: "Unknown",
                senderAvatar = currentUser.photoUrl ?: "",
                content = content,
                isRead = false
            )

            val roomRef =firestore.collection("rooms")
                .document(roomId)

            val batch = firestore.batch()
            batch.set(docRefs, message)
            batch.update(roomRef,
                mapOf(
                    "lastMessage" to message.content,
                    "lastMessageSenderId" to message.senderId,
                    "lastMessageTime" to FieldValue.serverTimestamp(),
                    "unreadCounts.${chosenUserId}" to FieldValue.increment(1)
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }

    }

    fun listenRoomMessages(roomId: String, pageSize: Int, startAfter: Long? = null) =
        callbackFlow {
            var query = firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("timestamp")
                .limit(pageSize.toLong())

            if (startAfter != null) query = query.startAfter(startAfter)

            val reg = query.addSnapshotListener { snap, err ->
                if (err != null) return@addSnapshotListener
                val list = snap?.toObjects(Message::class.java).orEmpty()
                trySend(list)
            }

            awaitClose { reg.remove() }
        }

    suspend fun updateRoomMessagesIsRead(roomId: String, currentUserId: String) : Result<Unit>{
        return try {
            val messagesQuery = firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .whereEqualTo("read", false)

            val snapshot = messagesQuery.get().await()

            val docsToUpdate = snapshot.documents.filter { doc ->
                doc.getString("senderId") != currentUserId
            }
            if (docsToUpdate.isEmpty()) return Result.success(Unit)
            //chia docsToUpdate thành cách batchDocs gồm 400 phần tử max,
            // sau đó góp 400 phần tử đó vào 1 batch và update cùng 1 lúc
            docsToUpdate.chunked(400).forEach { batchDocs ->
                val batch = firestore.batch()
                batchDocs.forEach { docSnapshot ->
                    batch.update(docSnapshot.reference, mapOf("isRead" to true))
                }
                batch.commit().await()
            }

            firestore.collection("rooms")
                .document(roomId)
                .update(mapOf("unreadCounts.$currentUserId" to 0))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}