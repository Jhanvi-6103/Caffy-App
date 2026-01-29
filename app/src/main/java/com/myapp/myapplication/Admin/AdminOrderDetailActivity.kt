package com.myapp.myapplication.Admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Admin.adapter.AdminOrderItemsAdapter
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.databinding.ActivityAdminOrderDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderDetailBinding
    private val db = FirebaseDatabase.getInstance().reference

    private lateinit var order: OrderModel
    private lateinit var userId: String
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: return
        orderId = intent.getStringExtra("orderId") ?: return

        binding.backBtn.setOnClickListener { finish() }

        loadOrder()
    }

    private fun loadOrder() {
        db.child("Orders")
            .child(userId)
            .child(orderId)
            .get()
            .addOnSuccessListener { snap ->
                val fetchedOrder = snap.getValue(OrderModel::class.java)
                if (fetchedOrder != null) {
                    order = fetchedOrder
                    bindOrder()
                }
            }
    }

    private fun bindOrder() {

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        binding.apply {

            // Order Info
            orderIdTxt.text = "Order #${order.orderId}"
            orderStatusTxt.text = order.status
            orderDateTxt.text = sdf.format(Date(order.timestamp))
            totalAmountTxt.text = "₹${order.totalAmount}"

            // Customer Info
            userNameTxt.text = order.userName
            phoneTxt.text = order.phone
            addressTxt.text = "${order.address}, ${order.city} - ${order.pincode}"

            // Price Breakdown
            subtotalTxt.text = "Subtotal: ₹${order.subtotal ?: 0}"
            taxTxt.text = "Tax: ₹${order.tax ?: 0}"
            deliveryTxt.text = "Delivery: ₹${order.delivery ?: 0}"
            discountTxt.text = "Discount: -₹${order.discountAmount}"

            // Items List
            itemsRecycler.layoutManager =
                LinearLayoutManager(this@AdminOrderDetailActivity)

            itemsRecycler.adapter =
                AdminOrderItemsAdapter(order.items)

            handleButtons()
        }
    }

    private fun handleButtons() {

        binding.apply {

            when (order.status) {

                "Pending" -> {
                    actionCard.visibility = View.VISIBLE
                    approveBtn.visibility = View.VISIBLE
                    rejectBtn.visibility = View.VISIBLE
                    markPaidBtn.visibility = View.GONE
                }

                "Approved" -> {
                    actionCard.visibility = View.VISIBLE
                    approveBtn.visibility = View.GONE
                    rejectBtn.visibility = View.GONE
                    markPaidBtn.visibility = View.VISIBLE
                }

                "Paid", "Rejected", "Cancelled" -> {
                    actionCard.visibility = View.GONE
                }
            }

            approveBtn.setOnClickListener {
                updateStatus("Approved")
            }

            rejectBtn.setOnClickListener {
                updateStatus("Rejected")
            }

            markPaidBtn.setOnClickListener {
                updateStatus("Paid")
            }
        }
    }

    private fun updateStatus(status: String) {
        db.child("Orders")
            .child(userId)
            .child(orderId)
            .child("status")
            .setValue(status)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Order marked as $status",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
    }
}
