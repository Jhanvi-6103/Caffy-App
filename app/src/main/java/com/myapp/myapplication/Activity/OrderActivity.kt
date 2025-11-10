package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomMenu()
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun initBottomMenu() {
        // --- SET ACTIVE STATE for Order button ---
        // Assumes you created an icon named 'btn_4_active'
        binding.orderIcon.setImageResource(R.drawable.btn_4_active)
        binding.orderText.setTextColor(ContextCompat.getColor(this, R.color.orange))

        // --- SET ALL CLICK LISTENERS ---
        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.cartBtn.setOnClickListener{
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
        binding.orderBtn.setOnClickListener {
            // Already on this page
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}