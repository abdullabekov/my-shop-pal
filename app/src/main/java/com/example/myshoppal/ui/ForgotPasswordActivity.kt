package com.example.myshoppal.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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

        setupActionBar()

        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()) {
                showProgressDialog(getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    hideProgressDialog()
                    if (it.isSuccessful) {
                        Toast.makeText(this, getString(R.string.email_sent_success), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        showSnackBar("Error: ${it.exception?.message}", true)
                    }
                }
            } else {
                showSnackBar(getString(R.string.err_msg_enter_email), true)
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarForgotPasswordActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }
    }
}