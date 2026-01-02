package com.myapp.myapplication.Rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Adapter.RedeemedAdapter
import com.myapp.myapplication.databinding.FragmentRedeemedBinding

class RedeemedFragment : Fragment() {

    private var _binding: FragmentRedeemedBinding? = null
    private val binding get() = _binding!!

    private val redeemedList = mutableListOf<RedeemedItem>()

    data class RedeemedItem(
        val rewardName: String,
        val coinsUsed: Int,
        val timestamp: Long,
        val used: Boolean
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRedeemedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerRedeemed.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRedeemed.adapter = RedeemedAdapter(redeemedList)

        loadRedeemedHistory()
    }

    private fun loadRedeemedHistory() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        FirebaseDatabase.getInstance().reference
            .child("users").child(uid).child("rewards").child("redeemed")
            .get()
            .addOnSuccessListener { snap ->

                redeemedList.clear()

                snap.children.forEach { entry ->
                    val key = entry.key ?: return@forEach
                    val rewardMap = entry.value as? Map<*, *> ?: return@forEach

                    val code = rewardMap["code"]?.toString() ?: ""
                    val cost = (rewardMap["cost"] as? Long)?.toInt() ?: 0
                    val used = rewardMap["used"] as? Boolean ?: false
                    val createdAt = rewardMap["createdAt"] as? Long ?: key.toLongOrNull() ?: 0L

                    redeemedList.add(
                        RedeemedItem(
                            rewardName = code,
                            coinsUsed = cost,
                            timestamp = createdAt,
                            used = used
                        )
                    )
                }

                redeemedList.sortByDescending { it.timestamp }

                binding.recyclerRedeemed.adapter?.notifyDataSetChanged()

                binding.emptyView.visibility =
                    if (redeemedList.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
