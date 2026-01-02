package com.myapp.myapplication.Rewards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.Adapter.RedeemAdapter
import com.myapp.myapplication.databinding.FragmentCoinsBinding

class CoinsFragment : Fragment() {

    private var _binding: FragmentCoinsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardManager: RewardManager

    data class RewardOption(
        val code: String,
        val label: String,
        val cost: Int
    )

    // YOUR STATIC OFFERS
    // Static redeem options (professional flat discounts)
    private val redeemList = listOf(
        RewardOption("5_OFF",  "₹5 OFF • 25 coins", 25),
        RewardOption("10_OFF", "₹10 OFF • 45 coins", 45),
        RewardOption("15_OFF", "₹15 OFF • 70 coins", 70),
        RewardOption("20_OFF", "₹20 OFF • 90 coins", 90),
        RewardOption("35_OFF", "₹35 OFF • 150 coins", 150),
        RewardOption("50_OFF", "₹50 OFF • 200 coins", 200)
    )



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoinsBinding.inflate(inflater, container, false)
        rewardManager = RewardManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadCoins()
        loadRedeemedStatus()
    }

    // ----------------------------------------------------------------------
    // Load redeemed items → pass them to adapter
    // ----------------------------------------------------------------------
    private fun loadRedeemedStatus() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        rewardManager.readRedeemed(uid) { redeemedList ->

            // Convert redeemed codes into set for quick lookup
            val usedCodes = redeemedList.map { it.code }.toSet()

            requireActivity().runOnUiThread {
                setupRecycler(usedCodes)
            }
        }
    }

    // ----------------------------------------------------------------------
    private fun setupRecycler(usedCodes: Set<String>) {

        binding.redeemRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = RedeemAdapter(
                redeemList,
                usedCodes
            ) { option ->
                redeemOption(option)
            }
        }
    }

    // ----------------------------------------------------------------------
    private fun redeemOption(option: RewardOption) {
        val uid = FirebaseAuth.getInstance().uid ?: return

        rewardManager.redeemReward(uid, option.cost, option.code) { ok, msg ->
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                if (ok) {
                    // reload UI after redemption
                    loadCoins()
                    loadRedeemedStatus()
                }
            }
        }
    }

    private fun loadCoins() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        rewardManager.readRewardsOnce(uid) { valid, snapshot ->
            requireActivity().runOnUiThread {
                binding.tvCoinBalance.text = snapshot.coins.toString()
                binding.validityNotice.visibility = if (valid) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
