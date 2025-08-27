package com.example.chatapp.ui.detail_chat_screen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemChatMessageBinding
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageStatus
import com.example.chatapp.domain.data.MessageType
import com.example.chatapp.utils.DateUtils
import com.example.chatapp.utils.setImageChatUrl
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessageListAdapter(
    private val currentUserId: String
) : ListAdapter<Message, MessageListAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private lateinit var binding: ItemChatMessageBinding

    private var avatarMap : Map<String, String?> = emptyMap()

    fun setAvatarMap(avatarMap: Map<String, String?>) {
        this.avatarMap = avatarMap
        for (i in 0 until itemCount) {
            val payload = "AVATAR_CHANGED"
            val message = getItem(i)
            val newAvatar = avatarMap[message.senderId]
            val oldAvatar = message.senderAvatar
            if (newAvatar != oldAvatar) {
                notifyItemChanged(i, payload)
            }
        }
    }



    inner class MessageViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, showDateSeparator: Boolean, dateText: String?, showTime: Boolean) {
            with(binding) {
                if (showDateSeparator && dateText != null) {
                    tvDateSeparator.visibility = View.VISIBLE
                    tvDateSeparator.text = dateText
                } else {
                    tvDateSeparator.visibility = View.GONE
                }


                // Xác định là tin gửi hay nhận
                val isSent = message.senderId == currentUserId

                llSentMessage.visibility = if (isSent) View.VISIBLE else View.GONE
                llReceivedMessage.visibility = if (!isSent) View.VISIBLE else View.GONE

                if (isSent) {
                    val sendingText = binding.root.context.getString(R.string.sending)
                    if(message.messageType == MessageType.IMAGE) {
                        ivSentImage.visibility = View.VISIBLE
                        ivSentImage.setImageChatUrl(message.content)
                        tvSentMessage.visibility = View.GONE
                    } else {
                        ivSentImage.visibility = View.GONE
                        tvSentMessage.visibility = View.VISIBLE
                    }
                    tvSentMessage.text = message.content
                    if (showTime) {
                        if(message.messageStatus == MessageStatus.SENDING) {
                            tvSentTime.text = sendingText
                        } else if (message.timestamp != null) {
                            tvSentTime.text = formatTime(message.timestamp)
                        } else {
                            tvSentTime.text = ""
                        }
                        tvSentTime.visibility = View.VISIBLE
                    } else {
                        tvSentTime.visibility = View.GONE
                    }

                } else {
                    if(message.messageType == MessageType.IMAGE) {
                        ivReceivedImage.visibility = View.VISIBLE
                        ivReceivedImage.setImageChatUrl(message.content)
                        tvReceivedMessage.visibility = View.GONE
                    } else {
                        ivReceivedImage.visibility = View.GONE
                        tvReceivedMessage.visibility = View.VISIBLE
                    }
                    tvReceivedMessage.text = message.content
                    if (showTime && message.timestamp != null) {
                        tvReceivedTime.text = formatTime(message.timestamp)
                        tvReceivedTime.visibility = View.VISIBLE
                    } else {
                        tvReceivedTime.visibility = View.GONE
                    }

                    // Load avatar
                    val userAvatar = avatarMap[message.senderId] ?: message.senderAvatar

                    Glide.with(civSenderAvatar)
                        .load(userAvatar)
                        .placeholder(R.drawable.ic_user_mail)
                        .into(civSenderAvatar)
                }

            }

        }
        fun bindMessageStatus(message: Message) {
            val sendingText = binding.root.context.getString(R.string.sending)
            if(message.timestamp != null) {
                binding.tvSentTime.text = formatTime(message.timestamp)
            } else if (message.messageStatus == MessageStatus.SENDING) {
                binding.tvSentTime.text = sendingText
            } else {
                binding.tvSentTime.visibility = View.GONE
            }
        }

        fun bindAvatar(message: Message) {
            if (message.senderId != currentUserId) {
                val userAvatar = avatarMap[message.senderId] ?: message.senderAvatar
                Glide.with(binding.civSenderAvatar)
                    .load(userAvatar)
                    .placeholder(R.drawable.ic_user_mail)
                    .into(binding.civSenderAvatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty()) {
            Log.d("MyLog - Adapter", "Partial bind at position $position | payloads: $payloads")
            if (payloads.contains("MESSAGE_STATUS_CHANGED")) {
                val message = getItem(position)
                holder.bindMessageStatus(message)
            } else if (payloads.contains("TIMESTAMP_CHANGED")) {
                //Timestamp sẽ được cập nhật trước sau đó mới update message status nên chỉ cần bind lại message status, tránh bìnd lại nhiều lần
            } else if (payloads.contains("AVATAR_CHANGED")) {
                val message = getItem(position)
                holder.bindAvatar(message)
            }
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val today = getString(holder.itemView.context, R.string.today)
        val yesterday = getString(holder.itemView.context, R.string.yesterday)
        val message = getItem(position)
        val showDateSeparator = shouldShowDateSeparator(position)
        val dateText = if (showDateSeparator && message.timestamp != null) formatDate(message.timestamp, today, yesterday) else null
        val showTime = shouldShowTime(position)
        Log.d("MyLog - Adapter", "Full bind at position $position - showDateSeparator: $showDateSeparator, dateText: $dateText, showTime: $showTime, message: ${message.uid}")
        Log.d("MyLog - Adapter", message.toString())
        holder.bind(message, showDateSeparator, dateText, showTime)
    }


    private fun shouldShowDateSeparator(position: Int): Boolean {
        if (position == 0) return getItem(position).timestamp != null
        val prevTs = getItem(position - 1).timestamp
        val currTs = getItem(position).timestamp
        if (prevTs == null || currTs == null) return false
        return !isSameDay(prevTs, currTs)
    }

    private fun isSameDay(ts1: Date, ts2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = ts1 }
        val cal2 = Calendar.getInstance().apply { time = ts2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun shouldShowTime(position: Int): Boolean {
        if (position == itemCount - 1) return true
        val curr = getItem(position)
        val next = getItem(position + 1)
        if (curr.timestamp == null || next.timestamp == null) return false
        return curr.senderId != next.senderId || !isSameDay(curr.timestamp, next.timestamp)
    }

    private fun formatDate(timestamp: Date, today: String, yesterday: String): String {
        val dateCalendar = Calendar.getInstance().apply { time = timestamp }
        if(DateUtils.isToday(dateCalendar)){
            return today
        }

        if(DateUtils.isYesterday(dateCalendar)){
            return yesterday
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun formatTime(timestamp: Date): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }

}