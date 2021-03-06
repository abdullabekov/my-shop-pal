package com.example.myshoppal.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshoppal.databinding.ItemDashboardLayoutBinding
import com.example.myshoppal.model.Product
import com.example.myshoppal.ui.ProductDetailsActivity
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_ID
import com.example.myshoppal.utils.Constants.EXTRA_PRODUCT_OWNER_ID
import com.example.myshoppal.utils.GlideLoader

class DashboardItemsListAdapter(
    private var dashboardItems: List<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemDashboardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dashboardItems[position]
        with((holder as MyViewHolder).binding) {
            GlideLoader(holder.itemView.context).loadPicture(item.image, ivDashboardItemImage)
            tvDashboardItemTitle.text = item.title
            tvDashboardItemPrice.text = "$${item.price}"
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT_ID, item.product_id)
            intent.putExtra(EXTRA_PRODUCT_OWNER_ID, item.user_id)
            holder.itemView.context.startActivity(intent)
        }
//        holder.itemView.setOnClickListener { onClickListener?.onClick(position, item) }
    }

    override fun getItemCount(): Int {
        return dashboardItems.size
    }

    class MyViewHolder(val binding: ItemDashboardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }
}