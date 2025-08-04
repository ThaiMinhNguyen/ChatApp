package com.example.chatapp.ui.new_chat_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemFriendSelectionBinding
import com.example.chatapp.domain.data.User

class FriendSelectionAdapter(
    private val onUserSelected: (User, Boolean) -> Unit
) : ListAdapter<User, FriendSelectionAdapter.FriendViewHolder>(UserDiffCallback()) {

    private val selectedUsers = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedUsers(): List<User> {
        return currentList.filter { user -> selectedUsers.contains(user.uid) }
    }

    fun removeUser(user: User) {
        selectedUsers.remove(user.uid)
        notifyItemChanged(currentList.indexOf(user))
    }


    inner class FriendViewHolder(
        private val binding: ItemFriendSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                tvFriendName.text = user.displayName ?: user.email ?: "Unknown User"

                Glide.with(civFriendAvatar)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_user_mail)
                    .error(R.drawable.ic_user_mail)
                    .into(civFriendAvatar)

                val isSelected = selectedUsers.contains(user.uid)
                rbSelect.isChecked = isSelected

                root.setOnClickListener {
                    toggleSelection(user)
                }

                rbSelect.setOnClickListener {
                    toggleSelection(user)
                }
            }
        }

        fun toggleSelection(user: User) {
            val isCurrentlySelected = selectedUsers.contains(user.uid)
            
            if (isCurrentlySelected) {
                selectedUsers.remove(user.uid)
                binding.rbSelect.isChecked = false
                onUserSelected(user, false)
            } else {
                selectedUsers.add(user.uid)
                binding.rbSelect.isChecked = true
                onUserSelected(user, true)
            }
        }
    }
}