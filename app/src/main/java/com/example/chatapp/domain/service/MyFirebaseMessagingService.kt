package com.example.chatapp.domain.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.transition.Transition
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.chatapp.R
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_NOTIFICATION_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_RECIPIENT_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.EXTRA_ROOM_ID
import com.example.chatapp.domain.service.NotificationReceiver.Companion.KEY_TEXT_REPLY
import com.example.chatapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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
        val content = message.data["content"] ?: ""
        val messageType = message.data["messageType"] ?: "TEXT"
        
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

        if(type == "chat") {
            val notify = NotificationCompat.Builder(this, "chat_messages")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(replyAction)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


            if (messageType == "IMAGE") {
                Log.d("MyLog - MessageService", "Loading image content: $content")
                val url =
                    "https://iqeeolueqpuuyizbecey.supabase.co/storage/v1/object/public/chat_image/$content"
                Glide.with(this.applicationContext)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>() {
                        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            notify.setLargeIcon(resource)
                                .setStyle(
                                    NotificationCompat.BigPictureStyle().bigPicture(resource)
                                        .bigLargeIcon(resource)
                                )
                            NotificationManagerCompat.from(this@MyFirebaseMessagingService)
                                .notify(notificationId, notify.build())
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            Log.e("MyLog - MessageService", "Failed to load image: $errorDrawable")
                        }
                    })
            } else {
                notify.setStyle(NotificationCompat.BigTextStyle().bigText(content))
                NotificationManagerCompat.from(this).notify(notificationId, notify.build())
            }
        } else if(type == "friend_request"){
            val notify = NotificationCompat.Builder(this, "chat_messages")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify.setStyle(NotificationCompat.BigTextStyle().bigText(content))
            NotificationManagerCompat.from(this).notify(notificationId, notify.build())
        }

    }

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .set(mapOf("fcmTokens" to FieldValue.arrayUnion(token)), SetOptions.merge())
    }
}