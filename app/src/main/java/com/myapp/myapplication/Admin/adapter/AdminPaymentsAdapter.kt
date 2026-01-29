package com.myapp.myapplication.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.databinding.ItemAdminPaymentBinding

class AdminPaymentsAdapter(
    private val list: List<OrderModel>
) : RecyclerView.Adapter<AdminPaymentsAdapter.ViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference

    inner class ViewHolder(val binding: ItemAdminPaymentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminPaymentBinding.inflate(
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
            statusChip.text = order.status.uppercase()

            btnReject.setOnClickListener {
                updateStatus(order.userId, order.orderId, "Rejected")
            }

            btnMarkPaid.setOnClickListener {
                updateStatus(order.userId, order.orderId, "Paid")
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
