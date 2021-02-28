package com.example.myshoppal.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myshoppal.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(imageSrc: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(imageSrc)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}