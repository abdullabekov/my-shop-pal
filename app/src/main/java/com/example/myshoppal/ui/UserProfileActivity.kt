package com.example.myshoppal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityUserProfileBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.User
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.Constants.FEMALE
import com.example.myshoppal.utils.Constants.GENDER
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
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

        user = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS) ?: User()

        with(binding) {
            etFirstName.isEnabled = false
            etFirstName.setText(user.firstName)
            etLastName.isEnabled = false
            etLastName.setText(user.lastName)
            etEmail.isEnabled = false
            etEmail.setText(user.email)

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
                showProgressDialog(getString(R.string.please_wait))
                FirestoreClass().uploadImageToCloudStorage(this@UserProfileActivity, selectedImageUri)
                if (validateUserProfile()) {
                    val userHashMap = HashMap<String, Any>()
                    val mobile = binding.etMobileNumber.text.toString().trim()
                    val gender = if (binding.rbMale.isChecked) MALE else FEMALE
                    userHashMap[MOBILE] = mobile.toLong()
                    userHashMap[GENDER] = gender

                    showProgressDialog(getString(R.string.please_wait))

                    FirestoreClass().updateUserProfileData(this@UserProfileActivity, userHashMap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
                        GlideLoader(this).loadUserPicture(it, binding.ivUserPhoto)
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(url: String) {
        hideProgressDialog()
        Toast.makeText(this, "Image uploaded $url", Toast.LENGTH_LONG).show()
    }
}