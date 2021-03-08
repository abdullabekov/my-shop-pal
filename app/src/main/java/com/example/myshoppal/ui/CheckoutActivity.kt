package com.example.myshoppal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityCheckoutBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Address
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.model.Order
import com.example.myshoppal.ui.adapters.CartItemsListAdapter
import com.example.myshoppal.utils.Constants.EXTRA_SELECTED_ADDRESS

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var address: Address? = null
    private var subTotal = 0.0
    private var total = 0.0
    private var cartItems: List<CartItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        address = intent.getParcelableExtra(EXTRA_SELECTED_ADDRESS)
        address?.let {
            binding.tvCheckoutAddressType.text = it.type
            binding.tvCheckoutFullName.text = it.name
            binding.tvCheckoutAddress.text = "${it.address} ${it.zipCode}"
            binding.tvCheckoutAdditionalNote.text = it.additionalNote
            if (it.otherDetails.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = it.otherDetails
            }
            binding.tvMobileNumber.text = it.mobileNumber

        }
        getProducts()

        binding.btnPlaceOrder.setOnClickListener { placeAnOrder() }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProducts() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    fun getProductSuccess(cartItems: List<CartItem>) {
        this.cartItems = cartItems
        hideProgressDialog()
        subTotal = cartItems.fold(0.0, { acc, item ->
            val stockQuantity = item.stock_quantity.toInt()
            val cartQuantity = item.cart_quantity.toInt()
            if (stockQuantity >= cartQuantity) {
                val amount = item.price.toDouble() * cartQuantity
                acc + amount
            } else {
                acc
            }
        })
        if (subTotal > 0) {
            binding.tvCheckoutSubTotal.text = "$$subTotal"
            binding.tvCheckoutShippingCharge.text = "$10.00"
            total = subTotal + 10
            binding.tvCheckoutTotalAmount.text = "$$total"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
        with(binding.rvCartListItems) {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            setHasFixedSize(true)
            this.adapter = CartItemsListAdapter(cartItems, true)
        }
    }

    private fun placeAnOrder(){
        showProgressDialog(getString(R.string.please_wait))
        val order = Order(
            FirestoreClass().getCurrentUserID(),
            cartItems,
            address!!,
            "My order ${System.currentTimeMillis()}",
            cartItems[0].image,
            subTotal.toString(),
            "10.0", // The Shipping Charge is fixed as $10 for now in our case.
            total.toString(),
            System.currentTimeMillis()
        )
        FirestoreClass().placeOrder(this, order)
    }

    fun orderPlaceSuccess(){
        FirestoreClass().updateCartDetails(this, cartItems)
    }

    fun allDetailUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "Your order was placed success!", Toast.LENGTH_LONG).show()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}