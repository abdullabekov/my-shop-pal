package com.example.myshoppal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityUserProfileBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.User
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.Constants.COMPLETE_PROFILE
import com.example.myshoppal.utils.Constants.FEMALE
import com.example.myshoppal.utils.Constants.FIRST_NAME
import com.example.myshoppal.utils.Constants.GENDER
import com.example.myshoppal.utils.Constants.IMAGE
import com.example.myshoppal.utils.Constants.LAST_NAME
import com.example.myshoppal.utils.Constants.MALE
import com.example.myshoppal.utils.Constants.MOBILE
import com.example.myshoppal.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.myshoppal.utils.Constants.REQUEST_READ_EXTERNAL_STORAGE_CODE
import com.example.myshoppal.utils.Constants.showImageChooser
import com.example.myshoppal.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var user: User
    private var selectedImageUri: Uri = Uri.EMPTY
    private var userProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS) ?: User()

        with(binding) {
            etFirstName.setText(user.firstName)
            etLastName.setText(user.lastName)
            etEmail.isEnabled = false
            etEmail.setText(user.email)
            if(user.gender == MALE) {
                rbMale.isChecked = true
            } else {
                rbFemale.isChecked = true
            }
            if (user.profileCompleted == 0) {
                tvTitle.text = getString(R.string.title_complete_profile)
                etFirstName.isEnabled = false
                etLastName.isEnabled = false
            } else {
                setupActionBar()
                tvTitle.text = getString(R.string.title_edit_profile)
                GlideLoader(this@UserProfileActivity).loadPicture(user.image, ivUserPhoto, R.drawable.ic_user_placeholder)
                if (user.mobile != 0L) {
                    etMobileNumber.setText(user.mobile.toString())
                }
            }

            ivUserPhoto.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@UserProfileActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@UserProfileActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE_CODE
                    )
                } else {
                    showImageChooser(this@UserProfileActivity)
                }
            }

            btnSubmit.setOnClickListener {
                if (validateUserProfile()) {
                    showProgressDialog(getString(R.string.please_wait))
                    /*
                    Если была загружена картинка, то выполнить процесс ее загрузки в
                    Cloud Storage. Затем будет выполнено обновление профиля в Firestore.
                    В противном случае необходиом только обновить профиль в  Firestore.
                     */
                    if (selectedImageUri != Uri.EMPTY) {
                        FirestoreClass().uploadImageToCloudStorage(
                            this@UserProfileActivity,
                            selectedImageUri, Constants.USER_PROFILE_IMAGE_PREFIX
                        )
                    } else {
                        updateUserProfile()
                    }
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updateUserProfile() {
        val userHashMap = HashMap<String, Any>()
        val firstName = binding.etFirstName.text.toString().trim()
        if (firstName != user.firstName) userHashMap[FIRST_NAME] = firstName
        val lastName = binding.etLastName.text.toString().trim()
        if (lastName != user.lastName) userHashMap[LAST_NAME] = lastName
        val mobile = binding.etMobileNumber.text.toString().trim()
        val gender = if (binding.rbMale.isChecked) MALE else FEMALE
        if (gender != user.gender) userHashMap[GENDER] = gender
        if (mobile.isNotEmpty() && mobile != user.mobile.toString()) userHashMap[MOBILE] =
            mobile.toLong()
        if (userProfileImageUrl.isNotEmpty()) {
            userHashMap[IMAGE] = userProfileImageUrl
        }
        userHashMap[COMPLETE_PROFILE] = 1
        FirestoreClass().updateUserProfileData(this@UserProfileActivity, userHashMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    data.data?.let {
                        selectedImageUri = it
                        GlideLoader(this).loadPicture(it, binding.ivUserPhoto, R.drawable.ic_user_placeholder)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        this, getString(R.string.image_selection_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun validateUserProfile(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim()) -> {
                showSnackBar(getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this, getString(R.string.msg_profile_update_success),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(url: String) {
        userProfileImageUrl = url
        updateUserProfile()
    }
}