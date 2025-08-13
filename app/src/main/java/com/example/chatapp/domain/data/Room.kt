package com.example.chatapp.domain.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Room(
    val participants: List<String>,
    val createdAt: Long,
    val lastMessage: String?,
    val lastMessageSenderId: String? = null,
    @ServerTimestamp val lastMessageTime: Date? = null,
    val roomType: RoomType,
    val roomName: String? = null,
    val roomAvatar: String? = null,
    val unreadCounts: Map<String, Int> = emptyMap()
) {
    constructor() : this(emptyList(), 0L, null, null, null, RoomType.PRIVATE)
}

enum class RoomType {
    PRIVATE,
    GROUP
}