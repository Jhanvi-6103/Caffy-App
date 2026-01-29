package com.myapp.myapplication.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.databinding.ItemAdminOrderSimpleBinding

class AdminTotalOrdersAdapter(
    private val list: List<OrderModel>,
    private val onClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<AdminTotalOrdersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAdminOrderSimpleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminOrderSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]

        holder.binding.apply {
            orderIdTxt.text = "Order #${order.orderId}"
            userNameTxt.text = order.userName
            totalTxt.text = "₹${order.totalAmount}"
            statusTxt.text = order.status

            root.setOnClickListener {
                onClick(order)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
