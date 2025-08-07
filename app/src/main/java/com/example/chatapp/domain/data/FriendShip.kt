package com.example.chatapp.domain.data

data class Friendship(
    val user1: String,
    val user2: String,
    val status: FriendshipStatus,
    val createdAt: Long,
    val acceptedAt: Long? = null,
    val requestedBy: String? = null
) {
    constructor() : this("", "", FriendshipStatus.PENDING, 0L)
}

enum class FriendshipStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    BLOCKED
}