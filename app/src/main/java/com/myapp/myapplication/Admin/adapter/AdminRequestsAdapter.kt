package com.myapp.myapplication.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.databinding.ItemAdminRequestBinding

class AdminRequestsAdapter(
    private val list: List<OrderModel>
) : RecyclerView.Adapter<AdminRequestsAdapter.ViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference

    inner class ViewHolder(val binding: ItemAdminRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]

        holder.binding.apply {

            orderIdTxt.text = "Order ID: ${order.orderId}"
            userNameTxt.text = order.userName
            totalTxt.text = "₹${order.totalAmount}"

            btnApprove.setOnClickListener {
                updateStatus(order.userId, order.orderId, "Approved")
            }

            btnReject.setOnClickListener {
                updateStatus(order.userId, order.orderId, "Rejected")
            }
        }
    }

    override fun getItemCount(): Int = list.size

    private fun updateStatus(userId: String, orderId: String, status: String) {
        db.child("Orders")
            .child(userId)
            .child(orderId)
            .child("status")
            .setValue(status)
    }
}
