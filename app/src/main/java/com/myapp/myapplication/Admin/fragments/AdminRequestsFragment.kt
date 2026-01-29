package com.myapp.myapplication.Admin.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.R
import com.myapp.myapplication.Admin.adapter.AdminRequestsAdapter
import com.myapp.myapplication.databinding.FragmentRequestsBinding

class AdminRequestsFragment : Fragment(R.layout.fragment_requests) {

    private lateinit var binding: FragmentRequestsBinding
    private lateinit var adapter: AdminRequestsAdapter
    private val db = FirebaseDatabase.getInstance().reference
    private val list = ArrayList<OrderModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRequestsBinding.bind(view)

        adapter = AdminRequestsAdapter(list)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = adapter

        loadPendingOrders()
    }

    private fun loadPendingOrders() {
        db.child("Orders").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for (user in snapshot.children) {
                    val userId = user.key ?: continue

                    for (orderSnap in user.children) {
                        val order =
                            orderSnap.getValue(OrderModel::class.java) ?: continue

                        if (order.status == "Pending") {
                            order.userId = userId   // 🔥 REQUIRED FOR APPROVE
                            list.add(order)
                        }
                    }
                }

                adapter.notifyDataSetChanged()  // ✅ NOW WORKS
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
