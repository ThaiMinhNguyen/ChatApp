package com.example.chatapp.utils

object UserUtils {
    fun generateId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }

    fun getOtherId(pair: String, myUid: String): String? {
        val parts = pair.split("_")
        return when {
            parts.size != 2 -> null
            parts[0] == myUid -> parts[1]
            parts[1] == myUid -> parts[0]
            else -> null
        }
    }
}