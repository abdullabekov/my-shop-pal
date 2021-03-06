package com.example.myshoppal.ui

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityRegisterBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.User
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            if(validateRegisterDetails()) {
                showProgressDialog(getString(R.string.please_wait))
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etEmail.text.toString(), binding.etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful){
                        val firebaseUser = it.result?.user
                        val user = User(
                            firebaseUser!!.uid,
                            binding.etFirstName.text.toString().trim { it <= ' ' },
                            binding.etLastName.text.toString().trim { it <= ' ' },
                            binding.etEmail.text.toString().trim { it <= ' ' }
                        )
                        FirestoreClass().registerUser(this@RegisterActivity, user)
                    } else {
                        hideProgressDialog()
                        showSnackBar("Error: ${it.exception?.message}", true)
                    }
                }
            }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_back_24)
        }

        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}