package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        prefsManager = PreferencesManager(this) // <-- Initialize this

        binding.startBtn.setOnClickListener {
            // !! THIS IS THE IMPORTANT CHANGE !!
            prefsManager.isFirstTime = false // Mark onboarding as complete

            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish this activity
        }
    }
}