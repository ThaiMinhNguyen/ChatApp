package com.example.chatapp.utils

object UserUtils {
    fun generateId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }
}