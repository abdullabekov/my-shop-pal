package com.example.myshoppal.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myshoppal.R
import com.example.myshoppal.databinding.FragmentDashboardBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.CartListActivity
import com.example.myshoppal.ui.SettingsActivity
import com.example.myshoppal.ui.adapters.DashboardItemsListAdapter

class DashboardFragment : BaseFragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(context, CartListActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(items: List<Product>) {
        hideProgressDialog()
        if (items.isNotEmpty()) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE
            with(binding.rvDashboardItems) {
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                val dashboardAdapter = DashboardItemsListAdapter(items)
                adapter = dashboardAdapter
//                dashboardAdapter.setOnClickListener(object : DashboardItemsListAdapter.OnClickListener {
//                    override fun onClick(position: Int, product: Product) {
//                        val intent = Intent(context, ProductDetailsActivity::class.java)
//                        intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
//                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, item.user_id)
//                        startActivity(intent)
//                    }
//                })

            }
        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getDashboardItems(this)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }
}