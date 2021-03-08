package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityMyOrderDetailsBinding
import com.example.myshoppal.model.Order
import com.example.myshoppal.ui.adapters.CartItemsListAdapter
import com.example.myshoppal.utils.Constants.EXTRA_MY_ORDER_DETAILS
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityMyOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        val order = intent.getParcelableExtra<Order>(EXTRA_MY_ORDER_DETAILS) ?: Order()
        setupUI(order)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(order: Order) {
        with(binding) {
            tvOrderDetailsId.text = order.title

            val dateFormat = "dd MMM yyyy HH:mm"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = order.order_datetime
            val orderDateTime = formatter.format(calendar.time)
            tvOrderDetailsDate.text = orderDateTime

            val diffInMilliSeconds: Long = System.currentTimeMillis() - order.order_datetime
            val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
            Log.d("Difference in Hours", "$diffInHours")

            when {
                diffInHours < 1 -> {
                    tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                    tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            this@MyOrderDetailsActivity,
                            R.color.colorAccent
                        )
                    )
                }
                diffInHours < 2 -> {
                    tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
                    tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            this@MyOrderDetailsActivity,
                            R.color.colorOrderStatusInProcess
                        )
                    )
                }
                else -> {
                    tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                    tvOrderStatus.setTextColor(
                        ContextCompat.getColor(
                            this@MyOrderDetailsActivity,
                            R.color.colorOrderStatusDelivered
                        )
                    )
                }
            }
            
            rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
            rvMyOrderItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(order.items, true)
            rvMyOrderItemsList.adapter = cartListAdapter

            tvMyOrderDetailsAddressType.text = order.address.type
            tvMyOrderDetailsFullName.text = order.address.name
            tvMyOrderDetailsAddress.text =
                "${order.address.address}, ${order.address.zipCode}"
            tvMyOrderDetailsAdditionalNote.text = order.address.additionalNote

            if (order.address.otherDetails.isNotEmpty()) {
                tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
                tvMyOrderDetailsOtherDetails.text = order.address.otherDetails
            } else {
                tvMyOrderDetailsOtherDetails.visibility = View.GONE
            }
            tvMyOrderDetailsMobileNumber.text = order.address.mobileNumber

            tvOrderDetailsSubTotal.text = order.sub_total_amount
            tvOrderDetailsShippingCharge.text = order.shipping_charge
            tvOrderDetailsTotalAmount.text = order.total_amount
        }
    }
}