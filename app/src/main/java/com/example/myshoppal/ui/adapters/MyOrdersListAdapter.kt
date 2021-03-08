package com.example.myshoppal.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemListLayoutBinding
import com.example.myshoppal.model.Order
import com.example.myshoppal.ui.MyOrderDetailsActivity
import com.example.myshoppal.utils.Constants.EXTRA_MY_ORDER_DETAILS
import com.example.myshoppal.utils.GlideLoader

class MyOrdersListAdapter(private val orders: List<Order>) : RecyclerView.Adapter<MyOrdersListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListLayoutBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val order = orders[position]
        with(holder.binding) {
            GlideLoader(holder.itemView.context).loadPicture(order.image, ivItemImage)
            tvItemName.text = order.title
            tvItemPrice.text = "$${order.total_amount}"
            ibDeleteProduct.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MyOrderDetailsActivity::class.java)
            intent.putExtra(EXTRA_MY_ORDER_DETAILS, order)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}