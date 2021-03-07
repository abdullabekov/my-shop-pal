package com.example.myshoppal.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityCartListBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.adapters.CartItemsListAdapter
import com.example.myshoppal.utils.Constants.EXTRA_SELECT_ADDRESS

class CartListActivity : BaseActivity() {
    private lateinit var binding: ActivityCartListBinding
    private lateinit var cartItems: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra(EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

    private fun getCartItems() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }

    fun successCartItemsList(items: List<CartItem>) {
        hideProgressDialog()
        this.cartItems = items
        if (cartItems.isNotEmpty()) {
            binding.tvNoCartItemFound.visibility = View.GONE
            binding.rvCartItemsList.apply {
                visibility = View.VISIBLE
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@CartListActivity)
                adapter = CartItemsListAdapter(cartItems, false)
            }
            val subTotal = this.cartItems
                .filter { it.stock_quantity != "0" }
                .fold(0.0, { acc, item ->
                acc + item.cart_quantity.toInt() * item.price.toDouble()
            })
            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE
                binding.tvSubTotal.text = "$$subTotal"
                binding.tvShippingCharge.text = "$10.00"
                binding.tvTotalAmount.text = "$${subTotal + 10}"
            } else {
                binding.llCheckout.visibility = View.GONE
            }
        } else {
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
            binding.rvCartItemsList.visibility = View.GONE
        }
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        getCartItems()
    }
}