package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.myapp.myapplication.Adapter.CartAdapter
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ChangeNumberItemsListener
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartList: ArrayList<ItemsModel>

    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartList = ArrayList()

        loadCartFromFirebase()
        initBottomMenu()

        binding.backBtn.setOnClickListener { finish() }

        binding.button3.setOnClickListener {
            startActivity(Intent(this, ProceedOrderActivity::class.java))
        }
    }

    /* --------------------------------------------------------------------
                        LOAD CART FROM FIREBASE
    -------------------------------------------------------------------- */
    private fun loadCartFromFirebase() {
        val uid = auth.uid ?: return

        db.child("Cart").child(uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartList.clear()

                    for (itemSnap in snapshot.children) {
                        val item = itemSnap.getValue(ItemsModel::class.java)
                        if (item != null) cartList.add(item)
                    }

                    initCartList()
                    calculateCart()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    /* --------------------------------------------------------------------
                        SETUP CART LIST
    -------------------------------------------------------------------- */
    private fun initCartList() {
        binding.listView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.listView.adapter = CartAdapter(
            cartList,
            this,
            object : ChangeNumberItemsListener {
                override fun onChanged() {
                    saveCartToFirebase()
                    calculateCart()
                }
            }
        )
    }

    /* --------------------------------------------------------------------
                        SAVE CART TO FIREBASE
    -------------------------------------------------------------------- */
    private fun saveCartToFirebase() {
        val uid = auth.uid ?: return
        db.child("Cart").child(uid).setValue(cartList)
    }

    /* --------------------------------------------------------------------
                        CALCULATE BILL
    -------------------------------------------------------------------- */
    private fun calculateCart() {
        var subtotal = 0.0

        for (item in cartList) {
            subtotal += (item.price * item.numberInCart)
        }

        if (subtotal == 0.0) {
            binding.totalFeeTxt.text = formatter.format(0)
            binding.totalTaxTxt.text = formatter.format(0)
            binding.deliveryTxt.text = formatter.format(0)
            binding.totalTxt.text = formatter.format(0)
            return
        }

        val tax = subtotal * 0.02
        val delivery = 15.0
        val total = subtotal + tax + delivery

        binding.totalFeeTxt.text = formatter.format(subtotal)
        binding.totalTaxTxt.text = formatter.format(tax)
        binding.deliveryTxt.text = formatter.format(delivery)
        binding.totalTxt.text = formatter.format(total)
    }

    /* --------------------------------------------------------------------
                        BOTTOM NAVIGATION
    -------------------------------------------------------------------- */
    private fun initBottomMenu() {
        binding.cartIcon.setImageResource(R.drawable.btn_2_active)
        binding.cartText.setTextColor(ContextCompat.getColor(this, R.color.orange))

        binding.explorerBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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
