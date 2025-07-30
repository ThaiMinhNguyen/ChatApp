package com.example.chatapp.ui.home_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.ChatOverview

class ChatOverviewDiffCallback : DiffUtil.ItemCallback<ChatOverview>(){

    override fun areItemsTheSame(oldItem: ChatOverview, newItem: ChatOverview): Boolean {
        return oldItem.conversationId == newItem.conversationId
    }

    override fun areContentsTheSame(oldItem: ChatOverview, newItem: ChatOverview): Boolean {
        return oldItem == newItem
    }
}