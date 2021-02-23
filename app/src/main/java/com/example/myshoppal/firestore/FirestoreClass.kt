package com.example.myshoppal.firestore

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.myshoppal.model.User
import com.example.myshoppal.ui.LoginActivity
import com.example.myshoppal.ui.RegisterActivity
import com.example.myshoppal.utils.Constants.LOGGED_IN_USERNAME
import com.example.myshoppal.utils.Constants.MY_PREFS
import com.example.myshoppal.utils.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

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

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
    }
}