package com.example.chatapp.domain.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID

data class Message(
    val uid: String = "",
    val localId: String = UUID.randomUUID().toString(),
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    @ServerTimestamp val timestamp: Date? = null,
    val messageStatus: MessageStatus = MessageStatus.SENDING,
    val isRead: Boolean = false
)

enum class MessageStatus {
    SENDING,
    SENT,
    SEND_FAILED
}

enum class MessageType {
    TEXT,
    IMAGE,
    EMOJI
}