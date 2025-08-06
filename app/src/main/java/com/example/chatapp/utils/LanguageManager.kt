package com.example.chatapp.utils

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import androidx.core.content.edit

object LanguageManager {

    private const val LANGUAGE_PREF = "language_pref"
    private const val LANGUAGE_KEY = "language_key"

    fun setLanguage(context: Context, languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)

        AppCompatDelegate.setApplicationLocales(appLocale)

        val sharedPref = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        sharedPref.edit { putString(LANGUAGE_KEY, languageCode) }
    }


    fun getLanguage(context: Context): String {
        val sharedPref = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    fun initializeLanguage(context: Context) {
        val savedLanguage = getLanguage(context)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(savedLanguage)

        AppCompatDelegate.setApplicationLocales(appLocale)

        Log.d("MyLog - Debug Language","=== Language Debug Info ===")
        Log.d("MyLog - Debug Language","Saved language: $savedLanguage")
        Log.d("MyLog - Debug Language","App locales: ${appLocale.toLanguageTags()}")
        Log.d("MyLog - Debug Language","${AppCompatDelegate.getApplicationLocales()}")
        Log.d("MyLog - Debug Language","==========================")
    }

    fun getCurrentLocale(): LocaleListCompat {
        return AppCompatDelegate.getApplicationLocales()
    }

    fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) {
            "en"
        } else {
            locales.get(0)!!.language
        }
    }

    fun debugLanguageInfo(context: Context) {
        val savedLanguage = getLanguage(context)
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val deviceLocale = context.resources.configuration.locales[0]

        Log.d("MyLog - Debug Language","=== Language Debug Info ===")
        Log.d("MyLog - Debug Language","Saved language: $savedLanguage")
        Log.d("MyLog - Debug Language","App locales: ${appLocales.toLanguageTags()}")
        Log.d("MyLog - Debug Language","Device locale: ${deviceLocale.language}")
        Log.d("MyLog - Debug Language","==========================")
    }
}