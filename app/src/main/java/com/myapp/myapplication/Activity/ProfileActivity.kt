package com.myapp.myapplication.Activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityProfileBinding
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var managmentCart: ManagmentCart   // Kept for logic only (no badge)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        managmentCart = ManagmentCart(this)

        loadUserProfile()
        initBottomMenu()

        // ❌ Removed updateCartBadge()

        binding.backBtn.setOnClickListener { finish() }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
        // ❌ Removed updateCartBadge()
    }

    /* ----------------------------------------------------
            LOAD USER PROFILE
    ---------------------------------------------------- */
    private fun loadUserProfile() {
        val uid = auth.uid ?: return
        val user = auth.currentUser

        val userRef = database.getReference("users").child(uid)

        userRef.get().addOnSuccessListener {

            val name = it.child("name").value?.toString() ?: "User"
            val email = it.child("email").value?.toString() ?: ""
            val phone = it.child("phone").value?.toString() ?: ""
            val code = it.child("countryCode").value?.toString() ?: ""

            val firebaseImage = it.child("imageUrl").value?.toString()
            val googleImage = user?.photoUrl?.toString()

            binding.userName.text = name
            binding.userEmail.text = email
            binding.userPhone.text =
                if (phone.isNotEmpty()) "$code $phone" else "Phone Not Added"

            // Priority: Local → Google → Firebase → Default
            val localFile = File(filesDir, "person_profile.jpg")
            if (localFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(localFile.path)
                binding.profileImage.setImageBitmap(bitmap)
                return@addOnSuccessListener
            }

            if (!googleImage.isNullOrEmpty()) {
                Glide.with(this).load(googleImage).into(binding.profileImage)
                return@addOnSuccessListener
            }

            if (!firebaseImage.isNullOrEmpty()) {
                Glide.with(this).load(firebaseImage).into(binding.profileImage)
                return@addOnSuccessListener
            }

            binding.profileImage.setImageResource(R.drawable.person_profile)
        }
    }

    /* ----------------------------------------------------
            BOTTOM NAVIGATION
    ---------------------------------------------------- */
    private fun initBottomMenu() {

        binding.profileIcon.setImageResource(R.drawable.btn_5_active)
        binding.profileText.setTextColor(ContextCompat.getColor(this, R.color.orange))

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
    }
}
