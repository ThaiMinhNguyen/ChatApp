package com.example.chatapp.ui.new_chat_screen

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.domain.data.User

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}