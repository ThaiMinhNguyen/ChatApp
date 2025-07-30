package com.example.chatapp.domain.data

data class ChatOverview(
    val conversationId: String = "",
    val contactId: String = "",
    val contactName: String = "",
    val contactAvatar: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessageType: MessageType = MessageType.TEXT,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false
)
{
    constructor() : this(
        conversationId = "",
        contactId = "",
        contactName = "",
        contactAvatar = "",
        lastMessage = "",
        lastMessageTime = System.currentTimeMillis(),
        lastMessageType = MessageType.TEXT,
        unreadCount = 0,
        isOnline = false,
        isTyping = false,
        isPinned = false,
        isMuted = false
    )
}