package com.myapp.myapplication.Activity

import android.content.Intent // <-- 1. ADD THIS IMPORT
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // <-- 2. ADD THIS IMPORT
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapp.myapplication.Adapter.WishlistAdapter
import com.myapp.myapplication.Helper.WishlistManager
import com.myapp.myapplication.R // <-- 3. ADD THIS IMPORT
import com.myapp.myapplication.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var wishlistManager: WishlistManager
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wishlistManager = WishlistManager(this)

        initRecyclerView()
        binding.backBtn.setOnClickListener { finish() }
        initBottomMenu() // <-- 4. CALL THE NEW FUNCTION
    }

    private fun initRecyclerView() {
        val items = wishlistManager.getList()

        if (items.isEmpty()) {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.wishlistRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.wishlistRecyclerView.visibility = View.VISIBLE

            binding.wishlistRecyclerView.layoutManager = LinearLayoutManager(this)

            // --- Set up the new adapter ---
            wishlistAdapter = WishlistAdapter(items, this) { selectedItem ->
                // This is the delete click listener
                // Remove item from manager
                wishlistManager.removeItem(selectedItem)

                // Refresh the list (by calling this function again)
                initRecyclerView()
            }
            binding.wishlistRecyclerView.adapter = wishlistAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when coming back to the activity
        initRecyclerView()
    }

    // --- 5. ADD THIS ENTIRE FUNCTION ---
    private fun initBottomMenu() {
        // --- SET ACTIVE STATE for Wishlist button ---
        // Assumes you created an icon named 'btn_3_active'
        binding.wishlistIcon.setImageResource(R.drawable.btn_3_active)
        binding.wishlistText.setTextColor(ContextCompat.getColor(this, R.color.orange)) // Use your highlight color

        // --- SET ALL CLICK LISTENERS ---

        // 1. Explorer Button
        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2. Cart Button
        binding.cartBtn.setOnClickListener{
            startActivity(Intent(this, CartActivity::class.java))
        }

        // 3. Wishlist Button
        binding.wishlistBtn.setOnClickListener {
            // Already on this page, do nothing
        }

        // 4. Order Button
        binding.orderBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        // 5. Profile Button
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
