package com.example.chatapp.domain.data

data class Message(
    val uid: String = "",
    val roomId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
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
        timestamp = System.currentTimeMillis(),
        isRead = false
    )
}

enum class MessageType {
    TEXT,
    IMAGE,
    EMOJI
}