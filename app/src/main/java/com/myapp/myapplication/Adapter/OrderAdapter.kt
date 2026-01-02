package com.myapp.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.databinding.ViewholderOrderBinding
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val list: ArrayList<OrderModel>,
    private val onOrderClick: (OrderModel) -> Unit
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ViewholderOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]

        // ⭐ Format Date–Time
        val sdf = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        val dateText = sdf.format(Date(order.timestamp))

        // ⭐ Extract item names
        val itemNames = order.items.joinToString(", ") { it.title }

        holder.binding.orderId.text = "Order ID: ${order.orderId}"
        holder.binding.orderStatus.text = "Status: ${order.status}"
        holder.binding.orderAmount.text = "₹${order.totalAmount}"

        // ⭐ NEW → SHOW ITEM NAMES
        holder.binding.orderItems.text = "Items: $itemNames"

        // ⭐ NEW → SHOW DATE
        holder.binding.orderDate.text = dateText

        holder.binding.root.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = list.size
}
