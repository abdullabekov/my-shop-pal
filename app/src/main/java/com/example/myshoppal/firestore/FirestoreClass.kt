package com.example.myshoppal.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.myshoppal.model.User
import com.example.myshoppal.ui.*
import com.example.myshoppal.utils.Constants.LOGGED_IN_USERNAME
import com.example.myshoppal.utils.Constants.MY_PREFS
import com.example.myshoppal.utils.Constants.USERS
import com.example.myshoppal.utils.Constants.USER_PROFILE_IMAGE_PREFIX
import com.example.myshoppal.utils.Constants.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(registerActivity: RegisterActivity, user: User) {
        mFirestore.collection(USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnCompleteListener {
                registerActivity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                registerActivity.hideProgressDialog()
                Log.e(
                    registerActivity.javaClass.simpleName,
                    "Error while registering user", e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    fun getCurrentUser(activity: Activity) {
        mFirestore.collection(USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.toString())
                val user = it.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    MY_PREFS, Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is BaseActivity -> activity.hideProgressDialog()
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while updating user details", it)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, uri: Uri) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            USER_PROFILE_IMAGE_PREFIX + System.currentTimeMillis()
                    + "." + getFileExtension(activity, uri)
        )
        sRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL",
                taskSnapshot.metadata?.reference?.downloadUrl.toString())
                taskSnapshot.metadata?.reference?.downloadUrl
                    ?.addOnSuccessListener {
                        Log.e("Downloadable Image URL", it.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()

                    }
                }
                Log.e(activity.javaClass.simpleName, it.message, it)
            }
    }
}