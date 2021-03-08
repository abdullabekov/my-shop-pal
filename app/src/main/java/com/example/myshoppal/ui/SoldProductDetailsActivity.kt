package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivitySoldProductDetailsBinding
import com.example.myshoppal.model.SoldProduct
import com.example.myshoppal.utils.Constants.EXTRA_SOLD_PRODUCT_DETAILS
import com.example.myshoppal.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySoldProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        val soldProduct =
            intent.getParcelableExtra<SoldProduct>(EXTRA_SOLD_PRODUCT_DETAILS) ?: SoldProduct()

        setupUI(soldProduct)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSoldProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarSoldProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(productDetails: SoldProduct) {
        with(binding) {
            tvOrderDetailsId.text = productDetails.order_id

            // Date Format in which the date will be displayed in the UI.
            val dateFormat = "dd MMM yyyy HH:mm"
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = productDetails.order_date
            tvOrderDetailsDate.text = formatter.format(calendar.time)

            GlideLoader(this@SoldProductDetailsActivity).loadPicture(
                productDetails.image,
                ivProductItemImage
            )
            tvProductItemName.text = productDetails.title
            tvProductItemPrice.text = "$${productDetails.price}"
            tvSoldProductQuantity.text = productDetails.sold_quantity

            tvSoldDetailsAddressType.text = productDetails.address.type
            tvSoldDetailsFullName.text = productDetails.address.name
            tvSoldDetailsAddress.text =
                "${productDetails.address.address}, ${productDetails.address.zipCode}"
            tvSoldDetailsAdditionalNote.text = productDetails.address.additionalNote

            if (productDetails.address.otherDetails.isNotEmpty()) {
                tvSoldDetailsOtherDetails.visibility = View.VISIBLE
                tvSoldDetailsOtherDetails.text = productDetails.address.otherDetails
            } else {
                tvSoldDetailsOtherDetails.visibility = View.GONE
            }
            tvSoldDetailsMobileNumber.text = productDetails.address.mobileNumber

            tvSoldProductSubTotal.text = productDetails.sub_total_amount
            tvSoldProductShippingCharge.text = productDetails.shipping_charge
            tvSoldProductTotalAmount.text = productDetails.total_amount
        }
    }
}