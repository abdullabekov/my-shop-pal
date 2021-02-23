package com.example.myshoppal.firestore

import android.util.Log
import com.example.myshoppal.model.User
import com.example.myshoppal.ui.RegisterActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(registerActivity: RegisterActivity, user: User) {
        mFirestore.collection("users")
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
}