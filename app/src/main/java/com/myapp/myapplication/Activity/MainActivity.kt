package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // <-- 1. ADD THIS IMPORT
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.myapp.myapplication.Adapter.CategoryAdapter
import com.myapp.myapplication.Adapter.PopularAdapter
import com.myapp.myapplication.R
import com.myapp.myapplication.ViewModel.MainViewModel
import com.myapp.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel= MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initCategory()
        initPopular()
        initBottomMenu()
    }

    // --- 2. THIS FUNCTION IS NOW UPDATED ---
    private fun initBottomMenu() {

        // --- SET ACTIVE STATE for Explorer button ---
        // Assumes you created an icon named 'btn_1_active'
        binding.explorerIcon.setImageResource(R.drawable.btn_1_active)
        binding.explorerText.setTextColor(ContextCompat.getColor(this, R.color.orange)) // Use your highlight color

        // --- SET ALL CLICK LISTENERS ---

        // 1. Explorer Button
        binding.explorerBtn.setOnClickListener {
            // Already on this page, do nothing
        }

        // 2. Cart Button
        binding.cartBtn.setOnClickListener{
            startActivity(Intent(this, CartActivity::class.java))
        }

        // 3. Wishlist Button
        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        // 4. Order Button
        binding.orderBtn.setOnClickListener {
            // You need to create 'OrderActivity'
            // startActivity(Intent(this, OrderActivity::class.java))
        }

        // 5. Profile Button (navigates to MainActivity as requested)
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun initPopular() {
        binding.progressBarPopular.visibility=View.VISIBLE
        viewModel.loadPopular().observeForever{
            binding.recyclerViewPopular.layoutManager=GridLayoutManager(this,2)
            binding.recyclerViewPopular.adapter=PopularAdapter(it)
            binding.progressBarPopular.visibility=View.GONE
        }
        viewModel.loadPopular()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility=View.VISIBLE
        viewModel.loadCategory().observeForever{
            binding.categoryView.layoutManager=LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL,false
            )
            binding.categoryView.adapter=CategoryAdapter(it)
            binding.progressBarCategory.visibility=View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility= View.VISIBLE
        viewModel.loadBanner().observeForever{
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility= View.GONE
        }
        viewModel.loadBanner()
    }

}