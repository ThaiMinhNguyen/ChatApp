package com.example.chatapp.ui.home_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemChatBinding
import com.example.chatapp.databinding.ItemSearchChatBinding
import com.example.chatapp.domain.data.ChatListItem
import com.example.chatapp.domain.data.ChatOverview
import com.example.chatapp.domain.data.ChatSearchResult

class ChatListAdapter(val onItemClick: (String) -> Unit) : ListAdapter<ChatListItem, RecyclerView.ViewHolder>(ChatOverviewDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_CHAT = 0
        private const val VIEW_TYPE_SEARCH_RESULT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatOverview -> VIEW_TYPE_CHAT
            is ChatSearchResult -> VIEW_TYPE_SEARCH_RESULT
        }
    }

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

    inner class SearchResultViewHolder(private val binding: ItemSearchChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: ChatSearchResult) {
            with(binding) {
                root.setOnClickListener {
                    onItemClick(searchResult.conversationId)
                }

                tvUserName.text = searchResult.contactName

                // Load avatar
                Glide.with(civAvatar)
                    .load(searchResult.contactAvatar)
                    .placeholder(R.drawable.ic_user_mail)
                    .into(civAvatar)

                tvMessageCount.text = searchResult.messageMatch.toString() + " tin nhắn khớp"

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_CHAT -> {
                val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatViewHolder(binding)
            }
            VIEW_TYPE_SEARCH_RESULT -> {
                val binding = ItemSearchChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchResultViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatViewHolder -> holder.bind(getItem(position) as ChatOverview)
            is SearchResultViewHolder -> holder.bind(getItem(position) as ChatSearchResult)
            else -> throw IllegalArgumentException("Invalid view holder type")
        }
    }


}