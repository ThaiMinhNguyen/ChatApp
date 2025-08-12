package com.example.chatapp.domain.repository

import android.util.Log
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.Room
import com.example.chatapp.domain.data.RoomType
import com.example.chatapp.domain.data.User
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.UserUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createRoom(currentUser: User, chosenUser: User) : Result<Room>{
        return try {
            val roomId = UserUtils.generateId(currentUser.uid, chosenUser.uid)
            val room = Room(
                participants = listOf(currentUser.uid, chosenUser.uid),
                createdAt = DateUtils.getCurrentTimestamp(),
                lastMessage = null,
                lastMessageTime = null,
                roomType = RoomType.PRIVATE,
                roomName = null,
                roomAvatar = null
            )
            val docRefs = firestore.collection("rooms")
            docRefs.document(roomId).set(room).await()
            Result.success(room)
        } catch (e: Exception){
            Result.failure(e)
        }
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
            val updateRoom = room.copy(lastMessage = message.content, lastMessageTime = message.timestamp)
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

    suspend fun createMessage(currentUser: User, chosenUser: User, content: String) : Result<Message>{
        return try {
            val roomId = UserUtils.generateId(currentUser.uid, chosenUser.uid)
            val docRefs = firestore.collection("messages").document()
            val message = Message(
                uid = docRefs.id,
                roomId = roomId,
                senderId = "",
                senderName = "",
                senderAvatar = "",
                content = "",
                isRead = false
            )
            docRefs.set(message).await()
            Result.success(message)
        } catch (e: Exception){
            Result.failure(e)
        }

    }

}