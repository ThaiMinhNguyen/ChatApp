package com.example.chatapp

import android.app.Application
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
        Log.d("MyLog - Application", "${LanguageManager.getLanguage(this)}/${LanguageManager.getCurrentLanguageCode()}")
    }

}