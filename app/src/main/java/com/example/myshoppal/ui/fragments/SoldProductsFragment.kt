package com.example.myshoppal.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.FragmentSoldProductsBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.SoldProduct
import com.example.myshoppal.ui.adapters.SoldProductsListAdapter

class SoldProductsFragment : BaseFragment() {
    private var _binding: FragmentSoldProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSoldProducts()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getSoldProducts() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getSoldProductsList(this)
    }

    fun successGetSoldProductList(soldProducts: List<SoldProduct>) {
        hideProgressDialog()
        if(soldProducts.isEmpty()) {
            binding.tvNoSoldProductsFound.visibility = View.VISIBLE
            binding.rvSoldProductItems.visibility = View.GONE
        } else {
            binding.tvNoSoldProductsFound.visibility = View.GONE
            with(binding.rvSoldProductItems) {
                visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = SoldProductsListAdapter(soldProducts)
            }
        }
    }
}