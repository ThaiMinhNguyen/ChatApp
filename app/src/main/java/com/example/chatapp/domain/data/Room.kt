package com.example.chatapp.domain.data

data class Room(
    val participants: List<String>,
    val createdAt: Long,
    val lastMessage: String?,
    val lastMessageTime: Long?,
    val roomType: RoomType,
    val roomName: String? = null,
    val roomAvatar: String? = null
) {
    constructor() : this(emptyList(), 0L, null, null, RoomType.PRIVATE)
}

enum class RoomType {
    PRIVATE,
    GROUP
}