package com.example.chatapp.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chatapp.R


fun ImageView.setImageUrl(url: String?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_user_register)
        .error(R.drawable.ic_user_register)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}


fun ImageView.setImageChatUrl(url: String?) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_user_register)
        .error(R.drawable.ic_user_register)
        .override(350, 350)
        .fitCenter()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}