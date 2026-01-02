package com.myapp.myapplication.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityEditProfileBinding
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loadUserData()
        loadLocalOrGooglePhoto()

        binding.backBtn.setOnClickListener { finish() }
        binding.btnSaveChanges.setOnClickListener { saveProfile() }
    }

    // ---------------- LOAD PROFILE IMAGE ----------------
    private fun loadLocalOrGooglePhoto() {
        val googlePhoto = auth.currentUser?.photoUrl?.toString()
        val localFile = File(filesDir, "person_profile.jpg")

        when {
            localFile.exists() ->
                Glide.with(this).load(Uri.fromFile(localFile)).into(binding.editProfileImage)

            !googlePhoto.isNullOrEmpty() ->
                Glide.with(this).load(googlePhoto).into(binding.editProfileImage)
        }
    }

    // ---------------- LOAD USER DATA ----------------
    private fun loadUserData() {
        val uid = auth.uid ?: return

        database.getReference("users").child(uid).get()
            .addOnSuccessListener {

                binding.etEditName.setText(it.child("name").value.toString())
                binding.etEditEmail.setText(it.child("email").value.toString())

                val phone = it.child("phone").value?.toString() ?: ""
                binding.etEditPhone.setText(phone)

                val firebaseImage = it.child("imageUrl").value?.toString()
                val googlePhoto = auth.currentUser?.photoUrl?.toString()
                val localFile = File(filesDir, "person_profile.jpg")

                when {
                    localFile.exists() ->
                        Glide.with(this).load(Uri.fromFile(localFile)).into(binding.editProfileImage)

                    !googlePhoto.isNullOrEmpty() ->
                        Glide.with(this).load(googlePhoto).into(binding.editProfileImage)

                    !firebaseImage.isNullOrEmpty() ->
                        Glide.with(this).load(firebaseImage).into(binding.editProfileImage)

                    else -> binding.editProfileImage.setImageResource(R.drawable.person_profile)
                }
            }
    }

    // ---------------- SAVE PROFILE ----------------
    private fun saveProfile() {
        val name = binding.etEditName.text.toString().trim()
        val phone = binding.etEditPhone.text.toString().trim()

        if (name.length < 3) {
            binding.etEditName.error = "Invalid name"
            return
        }

        // phone optional but if entered must be valid
        if (phone.isNotEmpty() && phone.length < 6) {
            binding.etEditPhone.error = "Invalid phone number"
            return
        }

        val uid = auth.uid ?: return

        val map = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone
        )

        database.getReference("users").child(uid).updateChildren(map)

        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
