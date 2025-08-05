package com.example.chatapp.ui.friend_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemFriendRequestBinding
import com.example.chatapp.databinding.ItemHeaderBinding
import com.example.chatapp.domain.data.FriendListItem
import com.example.chatapp.domain.data.People

class FriendItemAdapter(
    private val onAddFriendClick: (People) -> Unit,
    private val onAcceptFriendClick: (People) -> Unit,
    private val onItemClick: (People) -> Unit
) : ListAdapter<FriendListItem, RecyclerView.ViewHolder>(FriendListDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_PERSON = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FriendListItem.Header -> TYPE_HEADER
            is FriendListItem.PersonItem -> TYPE_PERSON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            TYPE_PERSON -> {
                val binding = ItemFriendRequestBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PersonViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FriendListItem.Header -> {
                (holder as HeaderViewHolder).bind(item.title)
            }
            is FriendListItem.PersonItem -> {
                (holder as PersonViewHolder).bind(item.people)
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.tvHeaderTitle.text = title
        }
    }

    inner class PersonViewHolder(private val binding: ItemFriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(people: People) {
            with(binding) {
                tvFriendName.text = people.user.displayName ?: people.user.email ?: "Người dùng"

                if (people.user.photoUrl != null) {
                    Glide.with(itemView.context)
                        .load(people.user.photoUrl)
                        .placeholder(R.drawable.ic_user_mail)
                        .error(R.drawable.ic_user_mail)
                        .into(civFriendAvatar)
                } else {
                    civFriendAvatar.setImageResource(R.drawable.ic_user_mail)
                }

                when {
                    people.isFriend -> {
                        btnAction.visibility = android.view.View.GONE
                    }

                    people.isRequestSent -> {
                        btnAction.apply {
                            visibility = android.view.View.VISIBLE
                            text = "Hủy"
                            setBackgroundResource(R.drawable.btn_friend_request_cancel)
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.primary_blue
                                )
                            )
                            setOnClickListener {
                                onAddFriendClick(people)
                            }
                            isEnabled = true
                        }
                    }

                    people.isRequestReceived -> {
                        btnAction.apply {
                            visibility = android.view.View.VISIBLE
                            text = "Đồng ý"
                            setBackgroundResource(R.drawable.btn_friend_request)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            isEnabled = true
                            setOnClickListener {
                                onAcceptFriendClick(people)
                            }
                        }
                    }

                    else -> {
                        btnAction.apply {
                            visibility = android.view.View.VISIBLE
                            text = "Kết bạn"
                            setBackgroundResource(R.drawable.btn_friend_request)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            isEnabled = true
                            setOnClickListener {
                                onAddFriendClick(people)
                            }
                        }
                    }
                }

                itemView.setOnClickListener {
                    onItemClick(people)
                }
            }
        }
    }
}