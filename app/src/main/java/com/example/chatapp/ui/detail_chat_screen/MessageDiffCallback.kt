package com.example.chatapp.ui.detail_chat_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.Message

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }


}