package com.example.myshoppal.ui.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemAddressLayoutBinding
import com.example.myshoppal.model.Address
import com.example.myshoppal.ui.AddEditAddressActivity
import com.example.myshoppal.utils.Constants

class AddressListAdapter(private val addresses: List<Address>) :
    RecyclerView.Adapter<AddressListAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemAddressLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAddressLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(activity, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, addresses[position])
        activity.startActivity(intent)
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val address = addresses[position]
        with(holder.binding) {
            tvAddressFullName.text = address.name
            tvAddressType.text = address.type
            tvAddressDetails.text = "${address.address}, ${address.zipCode}"
            tvAddressMobileNumber.text = address.mobileNumber
        }
    }

    override fun getItemCount(): Int {
        return addresses.size
    }
}