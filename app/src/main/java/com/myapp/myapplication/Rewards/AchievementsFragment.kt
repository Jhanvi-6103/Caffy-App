package com.myapp.myapplication.Rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.Adapter.AchievementsAdapter
import com.myapp.myapplication.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardManager: RewardManager

    // --------------------------------------------------------
    // Achievement model
    // --------------------------------------------------------
    data class AchievementItem(
        val id: String,
        val title: String,
        val unlocked: Boolean,
        val description: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        rewardManager = RewardManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.achRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.btnRefresh.setOnClickListener { loadAchievements() }
        loadAchievements()
    }

    // --------------------------------------------------------
    // Load achievements from Firebase
    // --------------------------------------------------------
    private fun loadAchievements() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        rewardManager.readRewardsOnce(uid) { valid, snapshot ->

            requireActivity().runOnUiThread {

                val items = listOf(
                    AchievementItem(
                        id = "first_order",
                        title = "First Order",
                        unlocked = snapshot.achievements["first_order"] ?: false,
                        description = "Place your first order"
                    ),
                    AchievementItem(
                        id = "five_orders",
                        title = "Five Orders",
                        unlocked = snapshot.achievements["five_orders"] ?: false,
                        description = "Place 5 orders"
                    ),
                    AchievementItem(
                        id = "spent_500",
                        title = "Spent ₹500",
                        unlocked = snapshot.achievements["spent_500"] ?: false,
                        description = "Spend a total of ₹500"
                    )
                )

                binding.achRecycler.adapter =
                    AchievementsAdapter(items.toMutableList())

                // Show tampering notice if hash mismatch
                binding.invalidNotice.isVisible = !valid
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
