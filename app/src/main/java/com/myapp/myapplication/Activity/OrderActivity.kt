package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Adapter.OrderAdapter
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var managmentCart: ManagmentCart   // Cart manager kept only for logic, no badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        loadOrders()
        initBottomMenu()

        // ❌ Removed updateCartBadge()

        binding.backBtn.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        // ❌ Removed updateCartBadge()
    }

    /* ---------------------------------------------------------
                    LOAD ORDERS
    --------------------------------------------------------- */
    private fun loadOrders() {
        val uid = auth.uid ?: return

        database.child("users").child(uid)
            .get()
            .addOnSuccessListener { userSnap ->

                val username = userSnap.child("name").value?.toString() ?: "User"

                database.child("Orders").child(uid)
                    .get()
                    .addOnSuccessListener { snap ->

                        val list = ArrayList<OrderModel>()

                        for (order in snap.children) {
                            val orderModel = order.getValue(OrderModel::class.java) ?: continue

                            // Add username for old orders
                            if (orderModel.userName.isEmpty()) {
                                orderModel.userName = username
                                database.child("Orders")
                                    .child(uid)
                                    .child(orderModel.orderId)
                                    .child("userName")
                                    .setValue(username)
                            }

                            list.add(orderModel)
                        }

                        if (list.isEmpty()) {
                            binding.noOrdersText.visibility = View.VISIBLE
                            binding.recyclerOrders.visibility = View.GONE
                        } else {
                            binding.noOrdersText.visibility = View.GONE
                            binding.recyclerOrders.visibility = View.VISIBLE
                        }

                        list.sortByDescending { it.timestamp }

                        binding.recyclerOrders.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                        binding.recyclerOrders.adapter =
                            OrderAdapter(list) { selectedOrder ->
                                val intent = Intent(this, OrderSummaryActivity::class.java)
                                intent.putExtra("orderId", selectedOrder.orderId)
                                startActivity(intent)
                            }
                    }
            }
    }

    /* ---------------------------------------------------------
                    BOTTOM NAVIGATION
    --------------------------------------------------------- */
    private fun initBottomMenu() {

        binding.orderIcon.setImageResource(R.drawable.btn_4_active)
        binding.orderText.setTextColor(ContextCompat.getColor(this, R.color.orange))

        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
