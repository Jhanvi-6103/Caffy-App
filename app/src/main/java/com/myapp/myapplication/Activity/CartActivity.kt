package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.myapp.myapplication.Adapter.CartAdapter
import com.myapp.myapplication.Helper.ChangeNumberItemsListener
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var managmentCart: ManagmentCart
    private var tax: Double = 0.0
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        calculateCart()
        setVariable()
        initCartList()
        initBottomMenu() // <-- 1. CALL THE NEW FUNCTION
    }

    private fun initCartList() {
        binding.apply {
            listView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            listView.adapter = CartAdapter(
                managmentCart.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                }
            )
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 15.0
        tax = ((managmentCart.getTotalFee() * percentTax) * 100) / 100.0

        val total = ((managmentCart.getTotalFee() + tax + delivery) * 100) / 100
        val itemtotal = (managmentCart.getTotalFee() * 100) / 100

        binding.apply {
            totalFeeTxt.text = formatter.format(itemtotal)
            totalTaxTxt.text = formatter.format(tax)
            deliveryTxt.text = formatter.format(delivery)
            totalTxt.text = formatter.format(total)
        }
    }

    // --- 2. ADD THIS ENTIRE FUNCTION ---
    private fun initBottomMenu() {
        // --- SET ACTIVE STATE for Cart button ---
        // Assumes you created an icon named 'btn_2_active'
        binding.cartIcon.setImageResource(R.drawable.btn_2_active)
        binding.cartText.setTextColor(ContextCompat.getColor(this, R.color.orange)) // Use your highlight color

        // --- SET ALL CLICK LISTENERS ---

        // 1. Explorer Button
        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 2. Cart Button
        binding.cartBtn.setOnClickListener{
            // Already on this page, do nothing
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
