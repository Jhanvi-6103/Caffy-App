package com.myapp.myapplication.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ItemAdminOrderItemBinding

class AdminOrderItemsAdapter(
    private val list: List<ItemsModel>
) : RecyclerView.Adapter<AdminOrderItemsAdapter.VH>() {

    inner class VH(val binding: ItemAdminOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAdminOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.binding.apply {
            itemNameTxt.text = item.title
            itemQtyTxt.text = "Qty: ${item.numberInCart} • Size: ${item.size}"
            itemPriceTxt.text = "₹${item.price * item.numberInCart}"
        }
    }

    override fun getItemCount(): Int = list.size
}
