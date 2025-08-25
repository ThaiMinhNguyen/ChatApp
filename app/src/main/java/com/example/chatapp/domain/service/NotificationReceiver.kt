package com.example.chatapp.domain.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.chatapp.domain.data.Message
import com.example.chatapp.domain.data.MessageStatus
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.domain.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var authRepository: AuthRepository

    companion object {
        const val KEY_TEXT_REPLY = "key_text_reply"
        const val EXTRA_ROOM_ID = "extra_room_id"
        const val EXTRA_RECIPIENT_ID = "extra_recipient_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        val remoteInput = p1?.let { RemoteInput.getResultsFromIntent(it) }
        if(remoteInput != null){
            val remoteText = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString()
            val roomId = p1.getStringExtra(EXTRA_ROOM_ID)
            val recipientId = p1.getStringExtra(EXTRA_RECIPIENT_ID)
            val notificationId = p1.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
            if(roomId != null && recipientId != null) {
                sendReply(
                    roomId = roomId,
                    content = remoteText,
                    recipientId = recipientId,
                    appContext = p0?.applicationContext,
                    notificationId = notificationId
                )
            } else {
                Toast.makeText(p0, "Error: Please check your connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendReply(
        roomId: String,
        content: String,
        recipientId: String,
        appContext: Context?,
        notificationId: Int
    ){
        val currentUser = authRepository.getCurrentUser()
        if(currentUser == null){
            Log.e("MyLog - NotificationReceiver", "User not authenticated")
            return
        }
        val message = Message(
            roomId = roomId,
            localId = UUID.randomUUID().toString(),
            senderId = currentUser.uid,
            senderName = currentUser.displayName?: "Unknown",
            senderAvatar = currentUser.photoUrl.toString(),
            content = content,
            messageStatus = MessageStatus.SENDING
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = chatRepository.sendMessage(recipientId, message)
                    .onSuccess {
                        chatRepository.updateMessageStatus(message.roomId, message.localId)
                    }
                if (result.isSuccess) {
                    Log.d("MyLog - NotificationReceiver", "Message sent successfully")
                    if (appContext != null && notificationId != -1) {
                        NotificationManagerCompat.from(appContext).cancel(notificationId)
                    }
                } else {
                    Log.e("MyLog - NotificationReceiver", "Failed to send message")
                }
            } catch (e: Exception) {
                Log.e("MyLog - NotificationReceiver", "Error sending message: ${e.message}")
            }
        }
    }
}