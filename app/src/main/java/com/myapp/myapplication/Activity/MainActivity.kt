package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.myapp.myapplication.Adapter.CategoryAdapter
import com.myapp.myapplication.Adapter.PopularAdapter
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.R
import com.myapp.myapplication.ViewModel.MainViewModel
import com.myapp.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    // 1. FIXED: Changed ArrayList to MutableList
    private var masterItemsList: MutableList<ItemsModel> = mutableListOf()

    // Keep reference to category adapter to reset selection on resume
    private var categoryAdapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initCategory()
        // 2. This function now loads ALL items
        initAllItems()
        initBottomMenu()
        initSearch()
    }

    override fun onResume() {
        super.onResume()
        // Reset category selection highlight when coming back to this activity
        categoryAdapter?.resetSelection()
    }

    private fun initBottomMenu() {
        // --- SET ACTIVE STATE for Explorer button ---
        binding.explorerIcon.setImageResource(R.drawable.btn_1_active)
        binding.explorerText.setTextColor(ContextCompat.getColor(this, R.color.orange))

        // --- SET ALL CLICK LISTENERS ---
        binding.explorerBtn.setOnClickListener { /* Do nothing */ }
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
        binding.orderBtn.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // 3. This function is RENAMED from initPopular()
    private fun initAllItems() {
        binding.progressBarPopular.visibility = View.VISIBLE

        // 4. This now calls loadAllItems()
        viewModel.loadAllItems().observeForever {
            binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)

            // --- ⭐️ ADDED LIMIT 1 ---
            // Only show the first 15 items on initial load
            val initialList = it.take(15).toMutableList()
            binding.recyclerViewPopular.adapter = PopularAdapter(initialList)

            // 5. Store the FULL list for searching
            this.masterItemsList = it as MutableList<ItemsModel>

            binding.progressBarPopular.visibility = View.GONE
        }
        // 6. This now calls loadAllItems()
        viewModel.loadAllItems()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.categoryView.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )
            // Save adapter reference for resetting selection
            categoryAdapter = CategoryAdapter(it)
            binding.categoryView.adapter = categoryAdapter
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanner()
    }

    // --- 7. This is your search listener function ---
    private fun initSearch() {
        binding.editTextText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Call the filter function as user types
                filterList(s.toString())
            }
        })
    }

    // --- 8. This function now searches the MASTER list ---
    private fun filterList(query: String) {
        val isQueryEmpty = query.isEmpty()

        // Show/Hide main content based on search
        binding.banner.visibility = if (isQueryEmpty) View.VISIBLE else View.GONE
        binding.progressBarBanner.visibility = View.GONE
        binding.textView5.visibility = if (isQueryEmpty) View.VISIBLE else View.GONE // Category title
        binding.categoryView.visibility = if (isQueryEmpty) View.VISIBLE else View.GONE
        binding.popularTitleLayout.visibility = if (isQueryEmpty) View.VISIBLE else View.GONE

        if (isQueryEmpty) {
            // If query is empty, show first 15 items from the MASTER list

            // --- ⭐️ ADDED LIMIT 2 ---
            val initialList = masterItemsList.take(10).toMutableList()
            binding.recyclerViewPopular.adapter = PopularAdapter(initialList)
            binding.noResultsTxt.visibility = View.GONE
        } else {
            // If query is not empty, show filtered list
            // 9. FIXED: Changed ArrayList to mutableListOf
            val filteredList = mutableListOf<ItemsModel>()

            // 10. Search the MASTER list
            for (item in masterItemsList) {
                // Check if item title contains the query (case-insensitive)
                if (item.title.contains(query, ignoreCase = true)) {
                    filteredList.add(item)
                }
            }

            // --- ⭐️ ADDED LIMIT 3 ---
            // Only show the first 15 results from the filtered list
            val limitedFilteredList = filteredList.take(15).toMutableList()
            binding.recyclerViewPopular.adapter = PopularAdapter(limitedFilteredList)

            // Show "No results" message if the filtered list is empty
            if (filteredList.isEmpty()) { // Note: Check original filteredList for "no results"
                binding.noResultsTxt.visibility = View.VISIBLE
            } else {
                binding.noResultsTxt.visibility = View.GONE
            }
        }
    }
}
