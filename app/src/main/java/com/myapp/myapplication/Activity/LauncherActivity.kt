package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myapp.myapplication.utils.PreferencesManager

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefsManager = PreferencesManager(this)

        if (prefsManager.isFirstTime) {
            // First time: Go to Onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // Not first time: Go to Main
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Finish this LauncherActivity so the user can't navigate back to it
        finish()
    }
}