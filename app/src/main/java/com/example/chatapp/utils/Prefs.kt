package com.example.chatapp.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class Prefs @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        private const val PREF_NAME = "chatapp_prefs"
        private const val KEY_REMEMBER_LOGIN = "remember_login"
        private const val KEY_LAST_UID = "last_uid"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setRememberLogin(remember: Boolean) {
        sharedPrefs.edit { putBoolean(KEY_REMEMBER_LOGIN, remember) }
    }

    fun getRememberLogin(): Boolean {
        return sharedPrefs.getBoolean(KEY_REMEMBER_LOGIN, false)
    }

    fun saveLastUid(uid: String) {
        sharedPrefs.edit { putString(KEY_LAST_UID, uid) }
    }

    fun getLastUid(): String? {
        return sharedPrefs.getString(KEY_LAST_UID, null)
    }


    fun clear() {
        sharedPrefs.edit { clear() }
    }

}
