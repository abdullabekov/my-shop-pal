package com.example.myshoppal.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityCartListBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.adapters.CartItemsListAdapter

class CartListActivity : BaseActivity() {
    private lateinit var binding: ActivityCartListBinding
    private lateinit var products: List<Product>
    private lateinit var cartItems: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
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
//        getCartItems()
        getProductsList()
    }

    private fun getProductsList() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItems() {
        FirestoreClass().getCartList(this)
    }

    fun successCartItemsList(items: List<CartItem>) {
        hideProgressDialog()

        items.forEach { cartItem ->
            products.firstOrNull { cartItem.product_id == it.product_id }?.let {
                cartItem.stock_quantity = it.stock_quantity
                if (it.stock_quantity == "0") cartItem.cart_quantity = "0"
            }
        }

        this.cartItems = items

        if (cartItems.isNotEmpty()) {
            binding.tvNoCartItemFound.visibility = View.GONE
            binding.rvCartItemsList.apply {
                visibility = View.VISIBLE
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@CartListActivity)
                adapter = CartItemsListAdapter(cartItems)
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

    fun successProductsList(products: List<Product>) {
        this.products = products
        getCartItems()
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        getCartItems()
    }

    fun cartItemUpdateSuccess(){
        hideProgressDialog()
        getCartItems()
    }
}