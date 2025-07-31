package com.example.chatapp.ui.home_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemChatBinding
import com.example.chatapp.domain.data.ChatOverview

class ChatListAdapter(val onItemClick: (String) -> Unit) : ListAdapter<ChatOverview, ChatListAdapter.ChatViewHolder>(ChatOverviewDiffCallback()) {

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatOverview: ChatOverview) {
            with(binding){
                root.setOnClickListener {
                    onItemClick(chatOverview.conversationId)
                }

                tvUserName.text = chatOverview.contactName
                tvLastMessage.text = chatOverview.lastMessage
                tvTime.text = formatTime(chatOverview.lastMessageTime)

                if (chatOverview.unreadCount > 0) {
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                    tvUnreadCount.text = chatOverview.unreadCount.toString()
                } else {
                    tvUnreadCount.visibility = android.view.View.GONE
                }

            }
        }

        private fun formatTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60_000 -> "Vừa xong"
                diff < 3600_000 -> "${diff / 60_000}p"
                diff < 86400_000 -> "${diff / 3600_000}h"
                else -> "dd/MM"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}