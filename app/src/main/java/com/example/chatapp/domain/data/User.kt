package com.example.chatapp.domain.data

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val phoneNumber: String?,
    val dateOfBirth: String?,
    val isEmailVerified: Boolean
){
    constructor() : this("", null, null, null, null, null, false)
}


data class UserStatus(
    val uid: String,
    val isOnline: Boolean,
    val lastSeen: Long,
    val language: String,
    val notificationEnabled: Boolean,
    val theme: String // light, dark, auto
) {
    constructor() : this("", false, 0L, "en", true, "light")
}