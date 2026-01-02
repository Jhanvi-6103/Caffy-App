package com.myapp.myapplication.Rewards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.myapp.myapplication.databinding.ActivityRewardsBinding

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupTabs()
    }

    // --------------------------------------------------------
    // Setup ViewPager
    // --------------------------------------------------------
    private fun setupViewPager() {
        binding.viewPager.apply {
            adapter = RewardsPagerAdapter(this@RewardsActivity)
            setPageTransformer { page, position ->
                page.alpha = 0.2f + (1 - kotlin.math.abs(position))
            }
        }
    }

    // --------------------------------------------------------
    // Setup TabLayout with ViewPager2
    // --------------------------------------------------------
    private fun setupTabs() {
        val tabTitles = arrayOf("Coins", "Achievements", "Redeemed")

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = tabTitles[pos]   // ⭐ FIXED
        }.attach()
    }
}
