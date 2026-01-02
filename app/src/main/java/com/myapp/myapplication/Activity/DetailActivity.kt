package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.WishlistManager
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityDetailBinding
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var wishlistManager: WishlistManager

    private var selectedSize: String? = null
    private var selectedPrice: Int = 0

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wishlistManager = WishlistManager(this)

        bundle()
        initSizeSelection()
        initInfoPopup()
        initWishlistButton()
        initBottomMenu()
    }

    /* ---------------------------------------------------------
        SIZE SELECTION
    --------------------------------------------------------- */
    private fun initSizeSelection() {
        binding.apply {
            smallBtn.setBackgroundResource(0)
            mediumBtn.setBackgroundResource(0)
            largeBtn.setBackgroundResource(0)

            priceTxt.text = ""  // hide price initially

            smallBtn.setOnClickListener {
                selectedSize = "Small"
                highlightButton("Small")
                updatePrice()
            }

            mediumBtn.setOnClickListener {
                selectedSize = "Medium"
                highlightButton("Medium")
                updatePrice()
            }

            largeBtn.setOnClickListener {
                selectedSize = "Large"
                highlightButton("Large")
                updatePrice()
            }
        }
    }

    private fun highlightButton(size: String) {
        binding.apply {
            smallBtn.setBackgroundResource(0)
            mediumBtn.setBackgroundResource(0)
            largeBtn.setBackgroundResource(0)

            when (size) {
                "Small" -> smallBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                "Medium" -> mediumBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                "Large" -> largeBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
            }
        }
    }

    /* ---------------------------------------------------------
         UPDATE PRICE (ALWAYS TWO DECIMALS)
    --------------------------------------------------------- */
    private fun updatePrice() {
        if (selectedSize == null) {
            binding.priceTxt.text = ""
            return
        }

        val qty = binding.numberInCartTxt.text.toString().toInt()

        selectedPrice = when (selectedSize) {
            "Small" -> item.sizePrice?.small ?: item.price
            "Large" -> item.sizePrice?.large ?: item.price
            else -> item.sizePrice?.medium ?: item.price
        }

        val total = selectedPrice * qty

        // ⭐ ALWAYS show 2 digits after decimal
        val formatted = String.format(Locale.US, "%.2f", total.toDouble())

        binding.priceTxt.text = "₹$formatted"
    }


    /* ---------------------------------------------------------
        POPUP TOOLTIP
    --------------------------------------------------------- */
    private fun initInfoPopup() {
        binding.infoBtn.setOnClickListener { showPopup() }
        binding.closePopup.setOnClickListener { hidePopup() }
    }

    private fun showPopup() {
        val popup = binding.sizeInfoPopup

        binding.popupText.text =
            "Small: ₹${item.sizePrice?.small}\n" +
                    "Medium: ₹${item.sizePrice?.medium}\n" +
                    "Large: ₹${item.sizePrice?.large}"

        popup.alpha = 0f
        popup.translationY = 40f
        popup.visibility = View.VISIBLE

        popup.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(220)
            .start()
    }

    private fun hidePopup() {
        val popup = binding.sizeInfoPopup

        popup.animate()
            .alpha(0f)
            .translationY(40f)
            .setDuration(200)
            .withEndAction { popup.visibility = View.GONE }
            .start()
    }

    /* ---------------------------------------------------------
        LOAD ITEM DETAILS
    --------------------------------------------------------- */
    private fun bundle() {
        item = intent.getSerializableExtra("object") as ItemsModel

        binding.apply {

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            ratingTxt.text = item.rating.toString()

            priceTxt.text = "" // hide initially

            backBtn.setOnClickListener { finish() }

            plusBtn.setOnClickListener {
                item.numberInCart++
                numberInCartTxt.text = item.numberInCart.toString()
                updatePrice()
            }

            minusBtn.setOnClickListener {
                if (item.numberInCart > 1) {
                    item.numberInCart--
                    numberInCartTxt.text = item.numberInCart.toString()
                    updatePrice()
                }
            }

            addToCartBtn.setOnClickListener {

                if (selectedSize == null) {
                    Toast.makeText(
                        this@DetailActivity,
                        "Please select a coffee size",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                item.size = selectedSize
                item.price = selectedPrice
                item.numberInCart = numberInCartTxt.text.toString().toInt()

                saveCartToFirebase(item)

                Toast.makeText(this@DetailActivity, "Added to cart", Toast.LENGTH_SHORT).show()
            }

            checkWishlistStatus()
        }
    }

    /* ---------------------------------------------------------
        WISHLIST BUTTON
    --------------------------------------------------------- */
    private fun initWishlistButton() {
//
//        binding.favBtn.setOnClickListener { view ->
//
//            // 💗 Bounce Animation
//            view.animate()
//                .scaleX(1.4f)
//                .scaleY(1.4f)
//                .rotation(15f)
//                .setDuration(180)
//                .withEndAction {
//                    view.animate()
//                        .scaleX(1f)
//                        .scaleY(1f)
//                        .rotation(0f)
//                        .setDuration(180)
//                        .start()
//                }
//                .start()
//
//            if (wishlistManager.isWishlisted(item.title)) {
//                wishlistManager.removeItem(item)
//                binding.favBtn.setImageResource(R.drawable.btn_3)
//                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
//
//            } else {
//                wishlistManager.insertItem(item)
//                binding.favBtn.setImageResource(R.drawable.heart_filled)
//                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.favBtn.setOnClickListener { view ->

            // 🔄 Flip Animation
            view.animate()
                .rotationY(180f)
                .setDuration(300)
                .withEndAction {
                    view.rotationY = 0f
                }
                .start()

            if (wishlistManager.isWishlisted(item.title)) {
                wishlistManager.removeItem(item)
                binding.favBtn.setImageResource(R.drawable.btn_3)
                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show()
            } else {
                wishlistManager.insertItem(item)
                binding.favBtn.setImageResource(R.drawable.heart_filled)
                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun checkWishlistStatus() {
        if (wishlistManager.isWishlisted(item.title)) {
            binding.favBtn.setImageResource(R.drawable.heart_filled)
        } else {
            binding.favBtn.setImageResource(R.drawable.btn_3)
        }
    }

    /* ---------------------------------------------------------
        SAVE CART TO FIREBASE
    --------------------------------------------------------- */
    private fun saveCartToFirebase(item: ItemsModel) {
        val uid = auth.uid ?: return
        val itemId = item.title.replace(" ", "_")
        db.child("Cart").child(uid).child(itemId).setValue(item)
    }

    /* ---------------------------------------------------------
        BOTTOM NAVIGATION
    --------------------------------------------------------- */
    private fun initBottomMenu() {

        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

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
}
