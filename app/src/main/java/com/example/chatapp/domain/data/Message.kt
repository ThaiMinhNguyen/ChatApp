package com.example.chatapp.domain.data

import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class Message(
    val uid: String = "",
    @PrimaryKey val localId: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    @ServerTimestamp val timestamp: Date? = null,
    val messageStatus: MessageStatus = MessageStatus.SENDING,
    val read: Boolean = false
)

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    SEEN
}

enum class MessageType {
    TEXT,
    IMAGE,
    EMOJI
}