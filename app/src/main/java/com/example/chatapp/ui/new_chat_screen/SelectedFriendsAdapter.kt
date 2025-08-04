package com.example.chatapp.ui.new_chat_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemSelectedFriendBinding
import com.example.chatapp.domain.data.User

class SelectedFriendsAdapter(
    private val onRemoveFriend: (User) -> Unit
) : ListAdapter<User, SelectedFriendsAdapter.SelectedFriendViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedFriendViewHolder {
        val binding = ItemSelectedFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectedFriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SelectedFriendViewHolder(
        private val binding: ItemSelectedFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                Glide.with(civSelectedFriendAvatar)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_user_mail)
                    .error(R.drawable.ic_user_mail)
                    .into(civSelectedFriendAvatar)


                ivRemoveSelectedFriend.setOnClickListener {
                    onRemoveFriend(user)
                }

            }
        }
    }
}