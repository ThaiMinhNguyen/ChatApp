package com.example.chatapp.ui.home_screen

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemChatBinding
import com.example.chatapp.databinding.ItemSearchChatBinding
import com.example.chatapp.domain.data.ChatListItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatListAdapter(val onItemClick: (String) -> Unit) : ListAdapter<ChatListItem, RecyclerView.ViewHolder>(ChatListUiDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_ROOM = 0
        private const val VIEW_TYPE_SEARCH_RESULT = 1
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ChatListItem.RoomItem -> VIEW_TYPE_ROOM
        is ChatListItem.SearchResultItem -> VIEW_TYPE_SEARCH_RESULT
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListItem.RoomItem) {
            val room = item.room
            val roomId = room.participants.sorted().joinToString("_")
            with(binding) {
                root.setOnClickListener { onItemClick(roomId) }
                tvUserName.text = room.roomName ?: ""
                tvLastMessage.text = room.lastMessage ?: ""
                tvTime.text = room.lastMessageTime?.let { formatTime(it) } ?: ""
                if(item.unread > 0){
                    tvLastMessage.setTypeface(null, Typeface.BOLD)
                    tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                    tvUnreadCount.visibility = android.view.View.VISIBLE
                    tvUnreadCount.text = item.unread.toString()
                } else {
                    tvLastMessage.setTypeface(null, Typeface.NORMAL)
                    tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.icon_dark_gray))
                    tvUnreadCount.visibility = android.view.View.GONE
                }
                Glide.with(civAvatar)
                    .load(room.roomAvatar)
                    .placeholder(R.drawable.ic_user_register)
                    .error(R.drawable.ic_user_register)
                    .into(civAvatar)
            }
        }

        private fun formatTime(timestamp: Date): String {
            val nowMs = System.currentTimeMillis()
            val tsMs = timestamp.time
            val diffMs = nowMs - tsMs

            val min = binding.root.context.getString(R.string.min_unit)
            val hour = binding.root.context.getString(R.string.hour_unit)
            val recently = binding.root.context.getString(R.string.recently)

            return when {
                diffMs < 60_000L -> recently
                diffMs < 3_600_000L -> "${diffMs / 60_000L}$min"
                diffMs < 86_400_000L -> "${diffMs / 3_600_000L}$hour"
                else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(timestamp)
            }
        }
    }

    inner class SearchResultViewHolder(private val binding: ItemSearchChatBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ChatListItem.SearchResultItem) {
            with(binding) {
                root.setOnClickListener { onItemClick(item.roomId) }
                tvUserName.text = item.contactName
                Glide.with(civAvatar)
                    .load(item.contactAvatar)
                    .placeholder(R.drawable.ic_user_mail)
                    .into(civAvatar)
                tvMessageCount.text = item.messageMatch.toString() + " " + root.context.getString(R.string.chat_result_found)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ROOM -> ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_SEARCH_RESULT -> SearchResultViewHolder(ItemSearchChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChatListItem.RoomItem -> (holder as ChatViewHolder).bind(item)
            is ChatListItem.SearchResultItem -> (holder as SearchResultViewHolder).bind(item)
        }
    }
}