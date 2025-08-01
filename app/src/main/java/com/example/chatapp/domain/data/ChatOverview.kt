package com.example.chatapp.domain.data

sealed interface ChatListItem {
    val conversationId: String
    val contactId: String
    val contactName: String
    val contactAvatar: String
}

data class ChatOverview(
    override val conversationId: String = "",
    override val contactId: String = "",
    override val contactName: String = "",
    override val contactAvatar: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessageType: MessageType = MessageType.TEXT,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false
) : ChatListItem
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

data class ChatSearchResult(
    override val conversationId: String = "",
    override val contactId: String = "",
    override val contactName: String = "",
    override val contactAvatar: String = "",
    val messageMatch: Int
) : ChatListItem
{
    constructor() : this(
        conversationId = "",
        contactId = "",
        contactName = "",
        contactAvatar = "",
        messageMatch = 0
    )
}