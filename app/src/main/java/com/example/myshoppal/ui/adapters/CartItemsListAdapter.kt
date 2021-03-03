package com.example.myshoppal.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.file.FileResource
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ItemCartLayoutBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.ui.CartListActivity
import com.example.myshoppal.utils.Constants.CART_QUANTITY
import com.example.myshoppal.utils.GlideLoader

class CartItemsListAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<CartItemsListAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCartLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        with(holder.binding) {
            GlideLoader(context).loadPicture(item.image, ivCartItemImage)
            tvCartItemTitle.text = item.title
            tvCartItemPrice.text = "$${item.price}"
            if (item.cart_quantity == "0") {
                ibRemoveCartItem.visibility = View.GONE
                ibAddCartItem.visibility = View.GONE
                tvCartQuantity.text = context.getString(R.string.out_of_stock)
                tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.snackBarError
                    )
                )
            } else {
                ibRemoveCartItem.visibility = View.VISIBLE
                ibAddCartItem.visibility = View.VISIBLE
                tvCartQuantity.text = item.cart_quantity
            }

            ibDeleteCartItem.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.getString(R.string.please_wait))
                        FirestoreClass().removeItemFromCart(context, item.id)
                    }
                }
            }

            ibRemoveCartItem.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.getString(R.string.please_wait))
                        if (item.cart_quantity == "1") {
                            FirestoreClass().removeItemFromCart(context, item.id)
                        } else {
                            val cartQuantity = item.cart_quantity.toInt()
                            val itemHashMap = HashMap<String, Any>()
                            itemHashMap[CART_QUANTITY] = (cartQuantity - 1).toString()
                            FirestoreClass().updateCart(context, item.id, itemHashMap)
                        }
                    }
                }
            }

            ibAddCartItem.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        val cartQuantity = item.cart_quantity.toInt()
                        if (cartQuantity < item.stock_quantity.toInt()) {
                            context.showProgressDialog(context.getString(R.string.please_wait))
                            val itemHashMap = HashMap<String, Any>()
                            itemHashMap[CART_QUANTITY] = (cartQuantity + 1).toString()
                            FirestoreClass().updateCart(context, item.id, itemHashMap)
                        } else {
                            context.showSnackBar(context.getString(R.string.out_of_stock_msg,
                            item.stock_quantity), true)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}