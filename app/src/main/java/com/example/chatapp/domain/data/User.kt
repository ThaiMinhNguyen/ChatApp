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
