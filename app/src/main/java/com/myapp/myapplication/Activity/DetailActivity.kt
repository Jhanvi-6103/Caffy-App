package com.myapp.myapplication.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var managmentCart: ManagmentCart
    private var selectedSize: String? = null // Track selected size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        bundle()
        initSizeList()
    }

    private fun initSizeList() {
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
            priceTxt.text = "$${item.price}"
            ratingTxt.text = item.rating.toString()

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
        }
    }
}
