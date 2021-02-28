package com.example.myshoppal.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemListLayoutBinding
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.main.ProductsFragment
import com.example.myshoppal.utils.GlideLoader

open class MyProductsListAdapter(
    private val products: List<Product>,
    private val fragment: ProductsFragment
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = products[position]
        with((holder as MyViewHolder).binding) {
            GlideLoader(holder.itemView.context).loadPicture(model.image, ivItemImage)
            tvItemName.text = model.title
            tvItemPrice.text = "$${model.price}"
            ibDeleteProduct.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}