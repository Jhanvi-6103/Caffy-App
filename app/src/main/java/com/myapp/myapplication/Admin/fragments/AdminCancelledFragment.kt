package com.myapp.myapplication.Admin.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.R
import com.myapp.myapplication.Admin.adapter.AdminCancelledAdapter
import com.myapp.myapplication.databinding.FragmentCancelledBinding

class AdminCancelledFragment : Fragment(R.layout.fragment_cancelled) {

    private lateinit var binding: FragmentCancelledBinding
    private lateinit var adapter: AdminCancelledAdapter
    private val list = ArrayList<OrderModel>()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCancelledBinding.bind(view)

        adapter = AdminCancelledAdapter(list)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadCancelledOrders()
    }

    private fun loadCancelledOrders() {
        db.child("Orders").get().addOnSuccessListener { snap ->
            list.clear()

            for (user in snap.children) {
                for (orderSnap in user.children) {
                    val order =
                        orderSnap.getValue(OrderModel::class.java) ?: continue

                    if (order.status == "Cancelled") {
                        list.add(order)
                    }
                }
            }

            adapter.notifyDataSetChanged()
        }
    }
}
