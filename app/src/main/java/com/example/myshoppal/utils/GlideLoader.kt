package com.example.myshoppal.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myshoppal.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadPicture(imageSrc: Any, imageView: ImageView, placeholderResource: Int) {
        try {
            Glide.with(context)
                .load(imageSrc)
                .centerCrop()
                .placeholder(ContextCompat.getDrawable(context, placeholderResource))
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadPicture(imageSrc: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(imageSrc)
                .centerCrop()
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}