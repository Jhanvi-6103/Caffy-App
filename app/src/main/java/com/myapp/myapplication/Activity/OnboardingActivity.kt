package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.myapp.myapplication.Domain.OnboardingItem
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter
    private val onboardingItems = ArrayList<OnboardingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup your onboarding items
        // !! Replace with your actual drawables and text
        onboardingItems.add(
            OnboardingItem(
                R.drawable.sp, // Replace with your image
                "Welcome to Caffy",
                "All your favorite coffee, in one place."
            )
        )
        onboardingItems.add(
            OnboardingItem(
                R.drawable.sp1, // Replace with your image
                "Fast Delivery",
                "Get your coffee delivered to your doorstep in minutes."
            )
        )
        onboardingItems.add(
            OnboardingItem(
                R.drawable.sp5, // Replace with your image
                "Special Offers",
                "Enjoy daily boosts and special offers just for you."
            )
        )

        // 2. Setup Adapter
        adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        // 3. Setup Dots
        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { tab, position ->
            // No text
        }.attach()

        // 4. Setup Button Listeners
        binding.skipBtn.setOnClickListener {
            navigateToSplash()
        }

        binding.nextBtn.setOnClickListener {
            if (binding.viewPager.currentItem < adapter.itemCount - 1) {
                // Go to next slide
                binding.viewPager.currentItem += 1
            } else {
                // Last slide, go to SplashActivity
                navigateToSplash()
            }
        }

        // 5. Change "NEXT" to "FINISH" on last page
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    binding.nextBtn.text = "FINISH"
                } else {
                    binding.nextBtn.text = "NEXT"
                }
            }
        })
    }

    private fun navigateToSplash() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish() // Finish this activity so user can't go back to it
    }
}