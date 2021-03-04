package com.example.myshoppal.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivitySettingsBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.User
import com.example.myshoppal.utils.Constants.EXTRA_USER_DETAILS
import com.example.myshoppal.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.tvEdit.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }

        binding.llAddress.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getCurrentUser(this)
    }

    fun userDetailsSuccess(user: User) {
        hideProgressDialog()

        this.user = user

        GlideLoader(this).loadPicture(user.image, binding.ivUserPhoto, R.drawable.ic_user_placeholder)

        with (binding) {
            tvName.text = "${user.firstName} ${user.lastName}"
            tvGender.text = user.gender
            tvEmail.text = user.email
            tvMobileNumber.text = user.mobile.toString()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}