package com.example.myshoppal.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemListLayoutBinding
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.ProductDetailsActivity
import com.example.myshoppal.ui.fragments.ProductsFragment
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_ID
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_OWNER_ID
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
        val myHolder = (holder as MyViewHolder)
        with(myHolder.binding) {
            GlideLoader(holder.itemView.context).loadPicture(model.image, ivItemImage)
            tvItemName.text = model.title
            tvItemPrice.text = "$${model.price}"
            ibDeleteProduct.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }
        }
        myHolder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, model.product_id)
            intent.putExtra(EXTRA_PRODUCT_OWNER_ID, model.user_id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}