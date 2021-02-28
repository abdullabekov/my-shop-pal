package com.example.myshoppal.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.FragmentProductsBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.AddProductActivity
import com.example.myshoppal.ui.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_product -> {
                startActivity(Intent(context, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun successProductsListFromFirestore(products: List<Product>) {
        hideProgressDialog()
        val hasProducts = products.isNotEmpty()

        if(hasProducts) {
            binding.tvNoProductsFound.visibility = View.GONE
            with(binding.rvMyProductItems) {
                visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = MyProductsListAdapter(products)
            }
        } else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }

    private fun getProductsListFromFirestore() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductsListFromFirestore()
    }
}