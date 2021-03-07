package com.example.myshoppal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    // Collections in Firestore
    const val USERS = "users"
    const val PRODUCTS = "products"

    const val MY_PREFS = "MyPrefs"
    const val LOGGED_IN_USERNAME = "LoggedInUsername"
    const val EXTRA_USER_DETAILS = "ExtraUserDetails"

    const val REQUEST_READ_EXTERNAL_STORAGE_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MALE = "male"
    const val FEMALE = "female"
    const val MOBILE = "mobile"
    const val GENDER = "gender"
    const val IMAGE = "image"
    const val COMPLETE_PROFILE = "profileCompleted"

    const val PRODUCT_IMAGE = "Product_Image"

    const val USER_ID = "user_id"

    const val USER_PROFILE_IMAGE_PREFIX = "UserProfileImage"

    const val EXTRA_PRODUCT_ID = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID = "extra_product_owner_id"

    const val DEFAULT_CART_QUANTITY = "1"

    const val CART_ITEMS = "cart_items"

    const val PRODUCT_ID = "product_id"
    const val CART_QUANTITY = "cart_quantity"

    const val HOME = "Home"
    const val OFFICE = "Office"
    const val OTHER = "Other"

    const val ADDRESSES = "addresses"

    const val EXTRA_ADDRESS_DETAILS = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS = "extra_select_address"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}