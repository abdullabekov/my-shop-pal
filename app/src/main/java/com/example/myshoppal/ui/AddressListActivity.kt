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
import com.example.myshoppal.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvAddAddress.setOnClickListener {
            startActivity(Intent(this, AddEditAddressActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getAddresses()
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
                adapter = AddressListAdapter(addresses)

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