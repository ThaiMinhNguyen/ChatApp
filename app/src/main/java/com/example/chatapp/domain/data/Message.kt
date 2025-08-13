package com.example.chatapp.domain.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val uid: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    @ServerTimestamp val timestamp: Date? = null,
    val isRead: Boolean = false
) {
    constructor() : this(
        uid = "",
        roomId = "",
        senderId = "",
        senderName = "",
        senderAvatar = "",
        content = "",
        messageType = MessageType.TEXT,
        timestamp = null,
        isRead = false
    )
}

enum class MessageType {
    TEXT,
    IMAGE,
    EMOJI
}