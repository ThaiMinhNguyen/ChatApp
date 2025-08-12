package com.example.chatapp.domain.data

sealed interface ChatListItem {
    data class RoomItem(val room: Room) : ChatListItem
    data class SearchResultItem(
        val roomId: String,
        val contactId: String,
        val contactName: String,
        val contactAvatar: String,
        val messageMatch: Int
    ) : ChatListItem
}