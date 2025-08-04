package com.example.chatapp.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionUtils {
    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun checkImagePermission(context: Context): Boolean {
        return checkPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) &&
               checkPermission(context, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
    }

    fun requestImagePermission(context: Context): Boolean {
        return checkPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) &&
               checkPermission(context, android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
    }
}