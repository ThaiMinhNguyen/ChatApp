package com.example.chatapp.ui.detail_chat_screen

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
import com.example.chatapp.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MessageListAdapter(
    private val currentUserId: String
) : ListAdapter<Message, MessageListAdapter.MessageViewHolder>(MessageDiffCallback()) {

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
                    tvSentMessage.text = message.content
                    tvSentTime.text = formatTime(message.timestamp)
                    tvSentTime.visibility = if (showTime) View.VISIBLE else View.GONE
                } else {
                    tvReceivedMessage.text = message.content
                    tvReceivedTime.text = formatTime(message.timestamp)
                    tvReceivedTime.visibility = if (showTime) View.VISIBLE else View.GONE
                    // Load avatar
                    Glide.with(civSenderAvatar)
                        .load(message.senderAvatar)
                        .placeholder(com.example.chatapp.R.drawable.ic_user_mail)
                        .into(civSenderAvatar)
                }

            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val today = getString(holder.itemView.context, R.string.today)
        val yesterday = getString(holder.itemView.context, R.string.yesterday)
        val message = getItem(position)
        val showDateSeparator = shouldShowDateSeparator(position)
        val dateText = if (showDateSeparator) formatDate(message.timestamp, today, yesterday) else null
        val showTime = shouldShowTime(position)
        holder.bind(message, showDateSeparator, dateText, showTime)
    }


    private fun shouldShowDateSeparator(position: Int): Boolean {
        if (position == 0) return true
        val prev = getItem(position - 1)
        val curr = getItem(position)
        return !isSameDay(prev.timestamp, curr.timestamp)
    }

    private fun isSameDay(ts1: Long, ts2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = ts1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = ts2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun shouldShowTime(position: Int): Boolean {
        if (position == itemCount - 1) return true
        val curr = getItem(position)
        val next = getItem(position + 1)
        return curr.senderId != next.senderId || !isSameDay(curr.timestamp, next.timestamp)
    }

    private fun formatDate(timestamp: Long, today: String, yesterday: String): String {
        val dateCalendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        if(DateUtils.isToday(dateCalendar)){
            return today
        }

        if(DateUtils.isYesterday(dateCalendar)){
            return yesterday
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

}