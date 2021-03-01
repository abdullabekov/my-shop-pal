package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityProductDetailsBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Product
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_ID
import com.example.myshoppal.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private var productId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        productId = intent.getStringExtra(EXTRA_PRODUCT_ID) ?: ""

        getProductDetails()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, productId)
    }

    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()
        GlideLoader(this).loadPicture(product.image, binding.ivProductDetailImage)
        with(binding) {
            tvProductDetailsTitle.text = product.title
            tvProductDetailsDescription.text = product.description
            tvProductDetailsPrice.text = "$${product.price}"
            tvProductDetailsAvailableQuantity.text = product.stock_quantity
        }
    }
}