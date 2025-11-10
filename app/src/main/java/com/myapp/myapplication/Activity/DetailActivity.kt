package com.myapp.myapplication.Activity

import android.content.Intent // <-- 1. ADD THIS IMPORT
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // <-- 2. ADD THIS IMPORT
import com.bumptech.glide.Glide
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.Helper.WishlistManager
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var wishlistManager: WishlistManager
    private var selectedSize: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        wishlistManager = WishlistManager(this)

        bundle()
        initSizeList()
        initBottomMenu() // <-- 3. CALL THE NEW FUNCTION
    }

    private fun initSizeList() {
        // ... (your existing code is fine)
        binding.apply {

            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
                selectedSize = "Small" // Track selection
            }

            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                largeBtn.setBackgroundResource(0)
                selectedSize = "Medium" // Track selection
            }

            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                selectedSize = "Large" // Track selection
            }

        }
    }

    private fun bundle() {
        binding.apply {
            item = intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text = "₹${item.price}"
            ratingTxt.text = item.rating.toString()

            // --- Call function to set initial heart icon state ---
            checkWishlistStatus()

            addToCartBtn.setOnClickListener {
                if (selectedSize == null) {
                    Toast.makeText(this@DetailActivity, "Please select a coffee size first", Toast.LENGTH_SHORT).show()
                } else {
                    item.size = selectedSize
                    item.numberInCart = numberInCartTxt.text.toString().toInt()
                    managmentCart.insertItems(item)
                    Toast.makeText(this@DetailActivity, "${item.title} added to cart", Toast.LENGTH_SHORT).show()
                }
            }

            backBtn.setOnClickListener { finish() }

            plusBtn.setOnClickListener {
                numberInCartTxt.text = (item.numberInCart + 1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener {
                if (item.numberInCart > 0) {
                    numberInCartTxt.text = (item.numberInCart - 1).toString()
                    item.numberInCart--
                }
            }

            // --- OnClickListener for the heart (favBtn) ---
            favBtn.setOnClickListener {
                if (wishlistManager.isWishlisted(item.title)) {
                    // Item is already wishlisted, so remove it
                    wishlistManager.removeItem(item)
                    binding.favBtn.setImageResource(R.drawable.btn_3) // Set to empty heart
                    Toast.makeText(this@DetailActivity, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    // Item is not wishlisted, so add it
                    wishlistManager.insertItem(item)
                    binding.favBtn.setImageResource(R.drawable.heart_filled) // Set to filled heart
                    Toast.makeText(this@DetailActivity, "Added to wishlist", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // --- Function to check wishlist status ---
    private fun checkWishlistStatus() {
        if (wishlistManager.isWishlisted(item.title)) {
            binding.favBtn.setImageResource(R.drawable.heart_filled) // Set to filled heart
        } else {
            binding.favBtn.setImageResource(R.drawable.btn_3) // Set to empty heart
        }
    }

    // --- 4. ADD THIS ENTIRE FUNCTION ---
    private fun initBottomMenu() {
        // --- SET ACTIVE STATE ---
        // No button is active on the Detail page, so we leave this blank.

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
            startActivity(Intent(this, OrderActivity::class.java))
        }

        // 5. Profile Button
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
