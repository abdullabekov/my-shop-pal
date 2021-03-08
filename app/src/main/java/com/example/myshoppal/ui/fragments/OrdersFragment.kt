package com.example.myshoppal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.FragmentOrdersBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Order
import com.example.myshoppal.ui.adapters.MyOrdersListAdapter

class OrdersFragment : BaseFragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getMyOrders()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getMyOrders(){
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this)
    }

    fun ordersGetSuccess(orders: List<Order>){
        hideProgressDialog()
        if(orders.isEmpty()) {
            binding.tvNoOrdersFound.visibility = View.VISIBLE
            binding.rvMyOrderItems.visibility = View.GONE
        }
        binding.tvNoOrdersFound.visibility = View.GONE
        with(binding.rvMyOrderItems) {
            visibility = View.VISIBLE
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = MyOrdersListAdapter(orders)
        }
    }
}