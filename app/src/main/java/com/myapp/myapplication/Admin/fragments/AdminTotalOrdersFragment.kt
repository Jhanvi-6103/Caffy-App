package com.myapp.myapplication.Admin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.myapp.myapplication.Admin.AdminOrderDetailActivity
import com.myapp.myapplication.Admin.adapter.AdminTotalOrdersAdapter
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.FragmentTotalOrdersBinding

class AdminTotalOrdersFragment : Fragment(R.layout.fragment_total_orders) {

    private lateinit var binding: FragmentTotalOrdersBinding
    private lateinit var adapter: AdminTotalOrdersAdapter
    private val list = ArrayList<OrderModel>()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTotalOrdersBinding.bind(view)

        adapter = AdminTotalOrdersAdapter(list) { order ->
            openOrderDetails(order)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadAllOrders()
    }

    private fun loadAllOrders() {
        db.child("Orders").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for (userSnap in snapshot.children) {
                    val userId = userSnap.key ?: continue

                    for (orderSnap in userSnap.children) {
                        val order =
                            orderSnap.getValue(OrderModel::class.java) ?: continue
                        order.userId = userId
                        list.add(order)
                    }
                }

                binding.emptyLayout.visibility =
                    if (list.isEmpty()) View.VISIBLE else View.GONE

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openOrderDetails(order: OrderModel) {
        val intent = Intent(requireContext(), AdminOrderDetailActivity::class.java)
        intent.putExtra("userId", order.userId)
        intent.putExtra("orderId", order.orderId)
        startActivity(intent)
    }
}
