package com.myapp.myapplication.Admin.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Domain.OrderModel
import com.myapp.myapplication.R
import com.myapp.myapplication.admin.adapter.AdminPaymentsAdapter
import com.myapp.myapplication.databinding.FragmentPaymentsBinding

class AdminPaymentsFragment : Fragment(R.layout.fragment_payments) {

    private lateinit var binding: FragmentPaymentsBinding
    private lateinit var adapter: AdminPaymentsAdapter
    private val list = ArrayList<OrderModel>()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentsBinding.bind(view)

        adapter = AdminPaymentsAdapter(list)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadPayments()
    }

    private fun loadPayments() {
        db.child("Orders").get().addOnSuccessListener { snap ->
            list.clear()

            for (user in snap.children) {
                for (orderSnap in user.children) {
                    val order = orderSnap.getValue(OrderModel::class.java) ?: continue
                    if (order.status == "Approved" || order.status == "Paid") {
                        list.add(order)
                    }
                }
            }

            adapter.notifyDataSetChanged()
            binding.emptyLayout.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
