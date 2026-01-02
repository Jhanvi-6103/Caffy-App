package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapp.myapplication.Adapter.WishlistAdapter
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.Helper.WishlistManager
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityWishlistBinding

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var wishlistManager: WishlistManager
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var managmentCart: ManagmentCart   // kept for add-to-cart features but no badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wishlistManager = WishlistManager(this)
        managmentCart = ManagmentCart(this)

        initRecyclerView()
        binding.backBtn.setOnClickListener { finish() }
        initBottomMenu()
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    /* ---------------------------------------------------------
                        WISHLIST RECYCLER VIEW
    --------------------------------------------------------- */
    private fun initRecyclerView() {
        val items = wishlistManager.getList()   // ✅ Correct function

        if (items.isEmpty()) {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.wishlistRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.wishlistRecyclerView.visibility = View.VISIBLE

            binding.wishlistRecyclerView.layoutManager = LinearLayoutManager(this)

            wishlistAdapter = WishlistAdapter(items, this) { selectedItem ->
                wishlistManager.removeItem(selectedItem)
                initRecyclerView()
            }

            binding.wishlistRecyclerView.adapter = wishlistAdapter
        }
    }


    /* ---------------------------------------------------------
                        BOTTOM NAVIGATION MENU
    --------------------------------------------------------- */
    private fun initBottomMenu() {

        binding.wishlistIcon.setImageResource(R.drawable.btn_3_active)
        binding.wishlistText.setTextColor(ContextCompat.getColor(this, R.color.orange))

        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.wishlistBtn.setOnClickListener {
            // already here
        }

        binding.orderBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
