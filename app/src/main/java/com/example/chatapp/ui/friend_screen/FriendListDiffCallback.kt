package com.example.chatapp.ui.friend_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.FriendListItem

class FriendListDiffCallback : DiffUtil.ItemCallback<FriendListItem>() {

    override fun areItemsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
        return when {
            oldItem is FriendListItem.Header && newItem is FriendListItem.Header -> {
                oldItem.title == newItem.title
            }
            oldItem is FriendListItem.PersonItem && newItem is FriendListItem.PersonItem -> {
                oldItem.people.user.uid == newItem.people.user.uid
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: FriendListItem, newItem: FriendListItem): Boolean {
        return oldItem == newItem
    }
}