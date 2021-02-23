package com.example.myshoppal.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityLoginBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.User
import com.example.myshoppal.utils.Constants.EXTRA_USER_DETAILS
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        with(binding) {
            tvForgotPassword.setOnClickListener(this@LoginActivity)
            btnLogin.setOnClickListener(this@LoginActivity)
            tvRegister.setOnClickListener(this@LoginActivity)
        }
    }

    private fun validateSignInDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_login -> signIn()
                R.id.tv_forgot_password -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.tv_register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun signIn(){
        if (validateSignInDetails()) {
            showProgressDialog(getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    FirestoreClass().getCurrentUser(this)
                } else {
                    hideProgressDialog()
                    showSnackBar("Error: ${it.exception?.message.toString()}", true)
                }
            }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        Log.i("First name", user.firstName)
        Log.i("Last name", user.lastName)
        Log.i("Email", user.email)

        if (user.profileCompleted == 0) {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}