package com.myapp.myapplication.Activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Adapter.CategoryAdapter
import com.myapp.myapplication.Adapter.PopularAdapter
import com.myapp.myapplication.Adapter.RecommendedItemsAdapter
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.Rewards.RewardsActivity
import com.myapp.myapplication.ViewModel.MainViewModel
import com.myapp.myapplication.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private lateinit var managmentCart: ManagmentCart

    private var masterItemsList: MutableList<ItemsModel> = mutableListOf()

    // ---------- Typing Animation ----------
    private val typingHints = listOf(
        "Search Latte Art",
        "Search Dark Hot Chocolate",
        "Search Iced Cappuccino",
        "Search Mocha Americano"
    )

    private var textIndex = 0
    private var charIndex = 0
    private var deleting = false
    private var cursorVisible = true

    private val typingHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        loadUserDetails()
        initBanner()
        initCategory()
        initAllItems()
        initBottomMenu()
        initSearch()

        loadGifIcon()
        startTypingEffect()
        startCursorBlink()

        // ⭐ Redirect on GIF click
        binding.imageView8.setOnClickListener {
            startActivity(Intent(this, RewardsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserTopBar()
    }

    // ------------------ GIF LOADING ------------------
    private fun loadGifIcon() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.open_gift)
            .into(binding.imageView8)
    }

    // ------------------ USER DETAILS ------------------
    private fun loadUserDetails() {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid ?: return

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            binding.textView3.text = snapshot.child("name").value?.toString() ?: "User"

            val googlePhoto = auth.currentUser?.photoUrl?.toString()
            if (!googlePhoto.isNullOrEmpty()) {
                Glide.with(this).load(googlePhoto).into(binding.imageView7)
            }
        }
    }

    private fun loadUserTopBar() {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.uid ?: return

        val googlePhoto = auth.currentUser?.photoUrl?.toString()

        if (!googlePhoto.isNullOrEmpty()) {
            Glide.with(this).load(googlePhoto).into(binding.imageView7)
        } else {
            val file = File(filesDir, "profile_image.jpg")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                binding.imageView7.setImageBitmap(bitmap)
            } else {
                binding.imageView7.setImageResource(R.drawable.person_profile)
            }
        }

        FirebaseDatabase.getInstance().getReference("users")
            .child(uid)
            .get()
            .addOnSuccessListener {
                binding.textView3.text = it.child("name").value?.toString() ?: "User"
            }
    }

    // ------------------ BOTTOM NAVIGATION ------------------
    private fun initBottomMenu() {

        binding.explorerIcon.setImageResource(R.drawable.btn_1_active)
        binding.explorerText.setTextColor(ContextCompat.getColor(this, R.color.orange))

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

    // ------------------ POPULAR ITEMS ------------------
    private fun initAllItems() {
        binding.progressBarPopular.visibility = View.VISIBLE

        viewModel.loadAllItems().observeForever { list ->

            binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)

            binding.recyclerViewPopular.adapter = PopularAdapter(list.take(10).toMutableList())

            masterItemsList = list

            loadRecommendedItems()   // ⭐ ADDED

            binding.progressBarPopular.visibility = View.GONE
        }
    }

//     ------------------ RECOMMENDED ITEMS ------------------
//    private fun loadRecommendedItems() {
//        val uid = FirebaseAuth.getInstance().uid ?: return
//
//        FirebaseDatabase.getInstance()
//            .getReference("Orders")
//            .child(uid)
//            .get()
//            .addOnSuccessListener { snapshot ->
//
//                if (!snapshot.exists()) {
//                    // Hide recommended section if no orders
//                    binding.recommendedTitleLayout.visibility = View.GONE
//                    binding.recommendedFrame.visibility = View.GONE
//                    return@addOnSuccessListener
//                }
//
//                val orderCountMap = HashMap<String, Int>()
//
//                for (orderSnap in snapshot.children) {
//                    val itemsSnap = orderSnap.child("items")
//
//                    for (item in itemsSnap.children) {
//                        val title = item.child("title").value?.toString() ?: continue
//                        orderCountMap[title] = (orderCountMap[title] ?: 0) + 1
//                    }
//                }
//
//                val sortedTitles = orderCountMap
//                    .toList()
//                    .sortedByDescending { it.second }
//                    .map { it.first }
//
//                val recommendedItems = masterItemsList.filter {
//                    sortedTitles.contains(it.title)
//                }.take(6).toMutableList()
//
//                if (recommendedItems.isNotEmpty()) {
//                    binding.recommendedTitleLayout.visibility = View.VISIBLE
//                    binding.recommendedFrame.visibility = View.VISIBLE
//
//                    binding.recyclerViewRecommended.layoutManager =
//                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//
//                    binding.recyclerViewRecommended.adapter = RecommendedItemsAdapter(recommendedItems)
//                } else {
//                    binding.recommendedTitleLayout.visibility = View.GONE
//                    binding.recommendedFrame.visibility = View.GONE
//                }
//            }
//    }

    // ------------------ RECOMMENDED ITEMS ------------------
    private fun loadRecommendedItems() {
        val uid = FirebaseAuth.getInstance().uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Orders")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                // 🔹 NEW USER (NO ORDERS)
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    showPopularRecommendations()
                    return@addOnSuccessListener
                }

                val orderCountMap = HashMap<String, Int>()

                for (orderSnap in snapshot.children) {
                    val itemsSnap = orderSnap.child("items")

                    for (item in itemsSnap.children) {
                        val title = item.child("title").value?.toString() ?: continue
                        orderCountMap[title] = (orderCountMap[title] ?: 0) + 1
                    }
                }

                val sortedTitles = orderCountMap
                    .toList()
                    .sortedByDescending { it.second }
                    .map { it.first }

                val recommendedItems = masterItemsList.filter {
                    sortedTitles.contains(it.title)
                }.take(6).toMutableList()

                // 🔹 IF PERSONALIZED FOUND → SHOW
                if (recommendedItems.isNotEmpty()) {
                    binding.recommendedTitleLayout.visibility = View.VISIBLE
                    binding.recommendedFrame.visibility = View.VISIBLE

                    binding.recyclerViewRecommended.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

                    binding.recyclerViewRecommended.adapter =
                        RecommendedItemsAdapter(recommendedItems)
                } else {
                    // 🔹 FALLBACK TO POPULAR
                    showPopularRecommendations()
                }
            }
    }

    private fun showPopularRecommendations() {

        val popularItems = masterItemsList
            .sortedByDescending { it.rating }   // or popularity / ordersCount
            .take(7)
            .toMutableList()

        if (popularItems.isEmpty()) {
            binding.recommendedTitleLayout.visibility = View.GONE
            binding.recommendedFrame.visibility = View.GONE
            return
        }

        binding.recommendedTitleLayout.visibility = View.VISIBLE
        binding.recommendedFrame.visibility = View.VISIBLE

        binding.recyclerViewRecommended.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewRecommended.adapter =
            RecommendedItemsAdapter(popularItems)
    }



    // ------------------ CATEGORY ------------------
    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.loadCategory().observeForever {

            binding.categoryView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            binding.categoryView.adapter = CategoryAdapter(it)

            binding.progressBarCategory.visibility = View.GONE
        }
    }

    // ------------------ BANNER ------------------
    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE

        viewModel.loadBanner().observeForever { banners ->

            if (!banners.isNullOrEmpty()) {
                Glide.with(this).load(banners[0].url).into(binding.banner)
            } else {
                binding.banner.visibility = View.GONE
            }

            binding.progressBarBanner.visibility = View.GONE
        }
    }

    // ------------------ SEARCH FILTER ------------------
    private fun initSearch() {
        binding.editTextText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                filterResults(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterResults(query: String) {
        val empty = query.isEmpty()

        binding.banner.visibility = if (empty) View.VISIBLE else View.GONE
        binding.categoryView.visibility = if (empty) View.VISIBLE else View.GONE
        binding.popularTitleLayout.visibility = if (empty) View.VISIBLE else View.GONE
        binding.textView5.visibility = if (empty) View.VISIBLE else View.GONE

        if (empty) {
            binding.recyclerViewPopular.adapter =
                PopularAdapter(masterItemsList.take(10).toMutableList())
            binding.noResultsTxt.visibility = View.GONE
            return
        }

        val filtered = masterItemsList.filter {
            it.title.contains(query, ignoreCase = true)
        }.take(20).toMutableList()

        binding.recyclerViewPopular.adapter = PopularAdapter(filtered)

        binding.noResultsTxt.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    // ====================================================================
    //                         TYPING ANIMATION
    // ====================================================================

    private fun startTypingEffect() {
        typingHandler.post { animateHint() }
    }

    private fun animateHint() {
        val text = typingHints[textIndex]

        val safeIndex = charIndex.coerceIn(0, text.length)
        val partial = text.substring(0, safeIndex)

        val cursor = if (cursorVisible) "▌" else ""

        binding.editTextText.hint = partial + cursor

        if (!deleting) {
            if (charIndex < text.length) {
                charIndex++
                typingHandler.postDelayed({ animateHint() }, 70)
            } else {
                deleting = true
                typingHandler.postDelayed({ animateHint() }, 900)
            }
        } else {
            if (charIndex > 0) {
                charIndex--
                typingHandler.postDelayed({ animateHint() }, 40)
            } else {
                deleting = false
                textIndex = (textIndex + 1) % typingHints.size
                typingHandler.postDelayed({ animateHint() }, 400)
            }
        }
    }

    // ---------------- CURSOR BLINK ----------------
    private fun startCursorBlink() {
        typingHandler.post(object : Runnable {
            override fun run() {
                cursorVisible = !cursorVisible
                typingHandler.postDelayed(this, 450)
            }
        })
    }
}
