package com.myapp.myapplication.Rewards

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RewardsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3  // 3 tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CoinsFragment()
            1 -> AchievementsFragment()
            2 -> RedeemedFragment()   // new tab
            else -> CoinsFragment()
        }
    }
}
