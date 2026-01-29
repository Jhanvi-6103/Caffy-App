package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.myapp.myapplication.Admin.AdminMainActivity
import com.myapp.myapplication.databinding.ActivitySplashBinding
import com.myapp.myapplication.utils.PreferencesManager // <-- Import this

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var prefsManager: PreferencesManager // <-- Add this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PreferencesManager(this)

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val user = auth.currentUser

        // 🔹 CASE 1: First time user → show onboarding
        if (prefsManager.isFirstTime) {

            binding.startBtn.setOnClickListener {
                prefsManager.isFirstTime = false
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }

            return
        }

        // 🔹 CASE 2: Not logged in → Login
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 🔹 CASE 3: Logged in → check role
        val role = getSharedPreferences("MyCafePrefs", MODE_PRIVATE)
            .getString("user_role", "user") // DEFAULT user

        if (role == "admin") {
            startActivity(Intent(this, AdminMainActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }

}