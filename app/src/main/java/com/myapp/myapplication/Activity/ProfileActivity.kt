package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomMenu()
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun initBottomMenu() {
        // --- SET ACTIVE STATE for Profile button ---
        // Assumes you created an icon named 'btn_5_active'
        binding.profileIcon.setImageResource(R.drawable.btn_5_active)
        binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.orange))

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
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            // Already on this page
        }
    }
}