package com.example.chatapp.ui.home_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.ChatListItem

class ChatOverviewDiffCallback : DiffUtil.ItemCallback<ChatListItem>(){

    override fun areItemsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem.conversationId == newItem.conversationId
    }

    override fun areContentsTheSame(oldItem: ChatListItem, newItem: ChatListItem): Boolean {
        return oldItem == newItem
    }
}