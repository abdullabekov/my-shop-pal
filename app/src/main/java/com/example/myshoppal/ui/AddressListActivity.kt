package com.example.myshoppal.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityAddressListBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Address
import com.example.myshoppal.ui.adapters.AddressListAdapter
import com.example.myshoppal.utils.Constants
import com.example.myshoppal.utils.Constants.ADD_ADDRESS_REQUEST_CODE
import com.example.myshoppal.utils.Constants.EXTRA_SELECT_ADDRESS
import com.example.myshoppal.utils.SwipeToDeleteCallback
import com.example.myshoppal.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private var isAddressSelectedMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvAddAddress.setOnClickListener {
            startActivityForResult(
                Intent(this, AddEditAddressActivity::class.java),
                ADD_ADDRESS_REQUEST_CODE
            )
        }

        isAddressSelectedMode = intent.getBooleanExtra(EXTRA_SELECT_ADDRESS, false)
        if (isAddressSelectedMode) binding.tvTitle.text = getString(R.string.select_address)

        getAddresses()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ADDRESS_REQUEST_CODE && resultCode == RESULT_OK) {
            getAddresses()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successAddressFromFirestore(addresses: List<Address>) {
        hideProgressDialog()
        if (addresses.isNotEmpty()) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE
            with(binding.rvAddressList) {
                layoutManager = LinearLayoutManager(this@AddressListActivity)
                setHasFixedSize(true)
                adapter = AddressListAdapter(addresses, isAddressSelectedMode)

                val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            position = viewHolder.adapterPosition
                        )
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

                val deleteSwipeHandler =
                    object : SwipeToDeleteCallback(context = this@AddressListActivity) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            showProgressDialog(getString(R.string.please_wait))
                            FirestoreClass().deleteAddress(
                                this@AddressListActivity,
                                addresses[viewHolder.adapterPosition].id
                            )
                        }
                    }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
            }
        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

    private fun getAddresses() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAddresses(this)
    }
}