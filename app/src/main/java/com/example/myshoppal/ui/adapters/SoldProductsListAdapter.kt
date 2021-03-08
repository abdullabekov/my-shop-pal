package com.example.myshoppal.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemListLayoutBinding
import com.example.myshoppal.model.SoldProduct
import com.example.myshoppal.ui.SoldProductDetailsActivity
import com.example.myshoppal.utils.Constants.EXTRA_SOLD_PRODUCT_DETAILS
import com.example.myshoppal.utils.GlideLoader

class SoldProductsListAdapter(private val soldProducts: List<SoldProduct>) :
    RecyclerView.Adapter<SoldProductsListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListLayoutBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val soldProduct = soldProducts[position]
        with(holder.binding) {
            GlideLoader(holder.itemView.context).loadPicture(soldProduct.image, ivItemImage)
            tvItemName.text = soldProduct.title
            tvItemPrice.text = "$${soldProduct.price}"
            ibDeleteProduct.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SoldProductDetailsActivity::class.java)
            intent.putExtra(EXTRA_SOLD_PRODUCT_DETAILS, soldProduct)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return soldProducts.size
    }
}