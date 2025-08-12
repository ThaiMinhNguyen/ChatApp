package com.example.chatapp.ui.home_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.ChatListItem

class ChatListUiDiffCallback : DiffUtil.ItemCallback<ChatListItem>() {
    override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return when {
            oldItem is ChatListItem.RoomItem && newItem is ChatListItem.RoomItem ->
                oldItem.room.participants.sorted().joinToString("_") == newItem.room.participants.sorted().joinToString("_")
            oldItem is ChatListItem.SearchResultItem && newItem is ChatListItem.SearchResultItem ->
                oldItem.roomId == newItem.roomId && oldItem.messageMatch == newItem.messageMatch
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem == newItem
    }
}