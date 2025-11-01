package com.myapp.myapplication.Activity

import android.content.Intent // <-- 1. ADD THIS IMPORT
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // <-- 2. ADD THIS IMPORT
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.myapp.myapplication.Adapter.ItemListCategoryAdapter
import com.myapp.myapplication.R
import com.myapp.myapplication.ViewModel.MainViewModel
import com.myapp.myapplication.databinding.ActivityItemsListBinding

class ItemsListActivity : AppCompatActivity() {

    lateinit var binding: ActivityItemsListBinding
    private val viewModel=MainViewModel()

    private var id:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityItemsListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getBundles()
        initList()
        initBottomMenu() // <-- 3. CALL THE NEW FUNCTION
    }

    private fun initList() {
        binding.apply {
            progressBar3.visibility=View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemsListActivity, Observer {
                listView.layoutManager= GridLayoutManager(this@ItemsListActivity,2)

                listView.adapter= ItemListCategoryAdapter(it)
                progressBar3.visibility=View.GONE
            })
            backBtn.setOnClickListener{ finish()}
        }
    }

    private fun getBundles() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!

        binding.categoryTxt.text=title
    }

    // --- 4. ADD THIS ENTIRE FUNCTION ---
    private fun initBottomMenu() {
        // --- SET ACTIVE STATE ---
        // No button is active on this page.

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
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        // 4. Order Button
        binding.orderBtn.setOnClickListener {
            // You need to create 'OrderActivity'
            // startActivity(Intent(this, OrderActivity::class.java))
        }

        // 5. Profile Button (navigates to MainActivity)
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
