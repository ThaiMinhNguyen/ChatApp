package com.example.chatapp.ui.detail_chat_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.Message

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.localId == newItem.localId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Message, newItem: Message): Any? {
        return if (oldItem.messageStatus != newItem.messageStatus) {
            "MESSAGE_STATUS_CHANGED"
        } else if (
            oldItem.timestamp != newItem.timestamp
        ) {
            "TIMESTAMP_CHANGED"
        } else {
            null
        }
    }

}