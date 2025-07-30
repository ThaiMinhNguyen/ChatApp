package com.example.chatapp.domain.data

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val content: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    constructor() : this(
        id = "",
        conversationId = "",
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