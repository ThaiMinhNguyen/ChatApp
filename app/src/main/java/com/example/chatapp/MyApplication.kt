package com.example.chatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.example.chatapp.utils.LanguageManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LanguageManager.initializeLanguage(this)
        FirebaseApp.initializeApp(this)
        createNotificationChannel()
        Log.d("MyLog - Application", "${LanguageManager.getLanguage(this)}/${LanguageManager.getCurrentLanguageCode()}")
    }


    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            "chat_messages",
            "Chat messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for incoming chat messages"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }


}