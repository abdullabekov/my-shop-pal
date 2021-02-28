package com.example.myshoppal.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityAddProductBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Product
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.Constants.PRODUCT_IMAGE
import com.example.myshoppal.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private var selectedImageFileUri = Uri.EMPTY
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.ivAddUpdateProduct.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.REQUEST_READ_EXTERNAL_STORAGE_CODE
                )
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (validateProductDetails()) {
                uploadProductImage()
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_READ_EXTERNAL_STORAGE_CODE) {
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

        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    data.data?.let {
                        selectedImageFileUri = it
                        binding.ivAddUpdateProduct.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_vector_edit
                            )
                        )
                        try {
                            GlideLoader(this).loadPicture(it, binding.ivProductImage)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
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

    private fun validateProductDetails(): Boolean {
        return when {

            selectedImageFileUri == Uri.EMPTY -> {
                showSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, selectedImageFileUri, PRODUCT_IMAGE)
    }

    fun imageUploadSuccess(imageUrl: String) {
        this.imageUrl = imageUrl
        uploadProductDetails()
    }

    private fun uploadProductDetails() {
        val username = getSharedPreferences(
            Constants.MY_PREFS,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "") ?: ""
        with(binding) {
            val product = Product(
                FirestoreClass().getCurrentUserID(),
                username,
                title = etProductTitle.text.toString().trim(),
                price = etProductPrice.text.toString().trim(),
                description = etProductDescription.text.toString().trim(),
                stock_quantity = etProductQuantity.text.toString().trim(),
                image = imageUrl
            )

            FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
        }
    }

    fun productUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this, getString(R.string.product_upload_success_message),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
}