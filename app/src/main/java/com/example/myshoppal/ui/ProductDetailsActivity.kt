package com.example.myshoppal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityProductDetailsBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.model.Product
import com.example.myshoppal.utils.Constants.DEFAULT_CART_QUANTITY
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_ID
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_OWNER_ID
import com.example.myshoppal.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private var productId: String = ""
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        productId = intent.getStringExtra(EXTRA_PRODUCT_ID) ?: ""
        var productOwnerId = intent.getStringExtra(EXTRA_PRODUCT_OWNER_ID) ?: ""
        if (FirestoreClass().getCurrentUserID() == productOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.btnGoToCart.visibility = View.VISIBLE
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

        binding.btnGoToCart.setOnClickListener {
            startActivity(Intent(this, CartListActivity::class.java))
        }

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
        this.product = product
        hideProgressDialog()
        GlideLoader(this).loadPicture(product.image, binding.ivProductDetailImage)
        with(binding) {
            tvProductDetailsTitle.text = product.title
            tvProductDetailsDescription.text = product.description
            tvProductDetailsPrice.text = "$${product.price}"
            tvProductDetailsAvailableQuantity.text = product.stock_quantity
            if (product.stock_quantity == "0") {
                hideProgressDialog()
                btnAddToCart.visibility = View.GONE
                tvProductDetailsAvailableQuantity.text = getString(R.string.out_of_stock)
                tvProductDetailsAvailableQuantity.setTextColor(
                    ContextCompat.getColor(this@ProductDetailsActivity, R.color.snackBarError)
                )
            } else {
                btnAddToCart.visibility = View.VISIBLE
                if (FirestoreClass().getCurrentUserID() == product.user_id) {
                    hideProgressDialog()
                } else {
                    FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, productId)
                }
            }
        }
    }

    private fun addToCart() {
        val cart = CartItem(
            user_id = FirestoreClass().getCurrentUserID(),
            product_owner_id = product.user_id,
            product_id = productId,
            title = product.title,
            price = product.price,
            image = product.image,
            cart_quantity = DEFAULT_CART_QUANTITY
        )
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cart)
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this, getString(R.string.add_item_to_cart_success),
            Toast.LENGTH_LONG
        ).show()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}