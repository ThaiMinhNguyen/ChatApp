package com.example.chatapp.domain.service

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.RemoteInput
import com.example.chatapp.ui.MainActivity
import androidx.core.net.toUri
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_RECIPIENT_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_ROOM_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_NOTIFICATION_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.KEY_TEXT_REPLY

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("MyLog - MessageService", "onReceived: ${message.data}")
        
        val type = message.data["type"] ?: "chat"
        val title = message.data["title"] ?: "Tin nhắn mới"
        val body = message.data["body"] ?: "Bạn có tin nhắn mới"
        val roomId = message.data["chatConversationId"]
        val senderId = message.data["senderId"]
        val clickAction = message.data["click_action"]
        
        Log.d("MyLog - MessageService", "Type: $type")
        Log.d("MyLog - MessageService", "Click action: $clickAction")
        
        val pendingIntent = if (!clickAction.isNullOrEmpty()) {
            // Tạo Intent với Deep Link URL
            val intent = Intent(Intent.ACTION_VIEW, clickAction.toUri())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            // Fallback
            val intent = Intent(this, MainActivity::class.java)
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(this.getString(R.string.message_hint))
            .build()

        val notificationId = (roomId ?: System.currentTimeMillis().toString()).hashCode()

        val replyIntent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
            putExtra(EXTRA_RECIPIENT_ID, senderId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_mail_sign_in,
            this.getString(R.string.reply),
            replyPendingIntent)
            .addRemoteInput(remoteInput)
            .build()


        val notify = NotificationCompat.Builder(this, "chat_messages")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notify)
    }

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("fcmTokens" to FieldValue.arrayUnion(token)), SetOptions.merge())
    }
}