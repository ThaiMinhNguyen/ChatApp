package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.ChatListItem
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageStatus
import com.example.chatapp.domain.data.MessageType
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.RoomType
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
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
        var roomList : List<Room>
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

    suspend fun updateMessageStatus(roomId: String, localId: String) : Result<Unit> {
        return try {
            val messageRef = firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .whereEqualTo("localId", localId)
            val snapshot = messageRef.get().await()
            if (!snapshot.isEmpty) {
                snapshot.documents.forEach{ doc ->
                    doc.reference.update(
                        mapOf(
                            "messageStatus" to MessageStatus.SENT.name
                        )
                    ).await()
                }
            } else {
                return Result.failure(Exception("No message found with localId: $localId in room: $roomId"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MyLog - ChatRepo", "Error updating message image URL: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateMessageImageUrl(roomId: String, localId: String, imageUrl: String): Result<Unit> {
        return try {
            val messageRef = firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .whereEqualTo("localId", localId)
            val snapshot = messageRef.get().await()
            if (!snapshot.isEmpty) {
                snapshot.documents.forEach{ doc ->
                    doc.reference.update(
                        mapOf(
                            "content" to imageUrl,
                            "messageStatus" to MessageStatus.SENT.name
                        )
                    ).await()
                }
            } else {
                return Result.failure(Exception("No message found with localId: $localId in room: $roomId"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MyLog - ChatRepo", "Error updating message image URL: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun sendMessage(chosenUserId: String, message: Message) : Result<Unit>{
        if(message.content.isBlank()) return Result.success(Unit)
        return try {
            val docRefs = firestore
                .collection("rooms")
                .document(message.roomId)
                .collection("messages").document()

            val serverMessage = message.copy(
                uid = docRefs.id,
            )

            val roomRef = firestore.collection("rooms")
                .document(message.roomId)

            val batch = firestore.batch()
            batch.set(docRefs, serverMessage)
            val lastMessageValue = if (message.messageType == MessageType.IMAGE) {
                "Image"
            } else {
                message.content
            }
            batch.update(roomRef,
                mapOf(
                    "lastMessage" to lastMessageValue,
                    "lastMessageSenderId" to message.senderId,
                    "lastMessageTime" to FieldValue.serverTimestamp(),
                    "unreadCounts.${chosenUserId}" to FieldValue.increment(1)
                )
            )
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception){
            Log.e("MyLog - ChatRepo", e.toString())
            Result.failure(e)
        }

    }

    fun listenRoomMessages(roomId: String, pageSize: Int) =
        callbackFlow {
            val query = firestore.collection("rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

            val reg = query.addSnapshotListener { snap, err ->
                if (err != null) return@addSnapshotListener
                val list = snap?.toObjects(Message::class.java).orEmpty()
                val lastDoc = snap?.documents?.lastOrNull()
                trySend(list to lastDoc)
            }

            awaitClose { reg.remove() }
        }

    suspend fun loadMore(roomId: String, pageSize: Int = 20,
        oldestDoc: DocumentSnapshot? = null): Result<Pair<List<Message>, DocumentSnapshot?>> {
        return try {
            val ref = firestore.collection("rooms").document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val snap = (if (oldestDoc != null) ref.startAfter(oldestDoc) else ref)
                .limit(pageSize.toLong())
                .get()
                .await()

            val pageAsc = snap.toObjects(Message::class.java).asReversed()
            val lastDoc = snap.documents.lastOrNull()
            Result.success(pageAsc to lastDoc)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoomMessagesIsRead(roomId: String, currentUserId: String) : Result<Unit>{
        Log.d("MyLog - ChatRepo", "Updating messages to read in room $roomId for user $currentUserId")
        return try {
            firestore.collection("rooms")
                .document(roomId)
                .update(mapOf("unreadCounts.$currentUserId" to 0))
                .await()

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
                    batch.update(docSnapshot.reference, mapOf("read" to true))
                }
                batch.commit().await()
            }


            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMessagesMatch(
        currentUserId: String,
        query: String,
        limitPerRoom: Int = 20
    ): Result<List<ChatListItem.SearchResultItem>> {
        if (query.isBlank()) return Result.success(emptyList())
        return try {
            val roomsSnap = firestore.collection("rooms")
                .whereArrayContains("participants", currentUserId)
                .get()
                .await()

            val roomDocs = roomsSnap.documents
            if (roomDocs.isEmpty()) return Result.success(emptyList())

            val end = query + '\uf8ff'

            //Query từng room lấy match messages
            val results = coroutineScope {
                roomDocs.map { roomDoc ->
                    async {
                        val roomId = roomDoc.id

                        val participants = (roomDoc.get("participants") as? List<*>)?.filterIsInstance<String>().orEmpty()
                        val otherId = participants.firstOrNull { it != currentUserId } ?: currentUserId

                        val msgSnap = firestore.collection("rooms")
                            .document(roomId)
                            .collection("messages")
                            .orderBy("content")
                            .whereGreaterThanOrEqualTo("content", query)
                            .whereLessThanOrEqualTo("content", end)
                            .limit(limitPerRoom.toLong())
                            .get()
                            .await()

                        val matchCount = msgSnap.size()

                        if (matchCount > 0) {
                            ChatListItem.SearchResultItem(
                                roomId = roomId,
                                contactId = otherId,
                                contactName = "",
                                contactAvatar = "",
                                messageMatch = matchCount
                            )
                        } else null
                    }
                }.mapNotNull { it.await() }
            }

            val sorted = results.sortedByDescending { it.messageMatch }
            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenTopRoomsMessages(
        userId: String,
        limitRooms: Int = 10
    ): Flow<Map<String, Pair<List<Message>, DocumentSnapshot?>>> = callbackFlow {
        val registrations = mutableMapOf<String, ListenerRegistration>()
        val allMessages = mutableMapOf<String, Pair<List<Message>, DocumentSnapshot?>>()

        val roomsReg = firestore.collection("rooms")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .limit(limitRooms.toLong())
            .addSnapshotListener { roomsSnapshot, error ->
                if (error != null) {
                    Log.e("MyLog - ChatRepository", "Error listening top rooms: $error")
                    return@addSnapshotListener
                }

                val currentTopRoomIds = roomsSnapshot?.documents?.map { it.id }?.toSet() ?: emptySet()

                registrations.keys.toList().forEach { roomId ->
                    if (roomId !in currentTopRoomIds) {
                        registrations[roomId]?.remove()
                        registrations.remove(roomId)
                        allMessages.remove(roomId)
                    }
                }

                currentTopRoomIds.forEach { roomId ->
                    if (roomId !in registrations) {
                        val messagesReg = firestore.collection("rooms")
                            .document(roomId)
                            .collection("messages")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(20)
                            .addSnapshotListener { messagesSnapshot, messagesError ->
                                if (messagesError != null) {
                                    Log.e("MyLog - ChatRepository", "Error listening to messages for top room $roomId: $messagesError")
                                    return@addSnapshotListener
                                }
                                val desc = messagesSnapshot?.toObjects(Message::class.java).orEmpty()
                                val latestAsc = desc.asReversed()
                                val lastDoc = messagesSnapshot?.documents?.lastOrNull()
                                allMessages[roomId] = latestAsc to lastDoc
                                trySend(allMessages.toMap())
                            }
                        registrations[roomId] = messagesReg
                    }
                }

                trySend(allMessages.toMap())
            }

        awaitClose {
            registrations.values.forEach { it.remove() }
            roomsReg.remove()
        }
    }

}