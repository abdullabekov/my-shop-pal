package com.example.myshoppal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS = "users"
    const val MY_PREFS = "MyPrefs"
    const val LOGGED_IN_USERNAME = "LoggedInUsername"
    const val EXTRA_USER_DETAILS = "ExtraUserDetails"

    const val REQUEST_READ_EXTERNAL_STORAGE_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val MALE = "male"
    const val FEMALE = "female"
    const val MOBILE = "mobile"
    const val GENDER = "gender"

    const val USER_PROFILE_IMAGE_PREFIX = "UserProfileImage"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}