package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.Admin.AdminMainActivity
import com.myapp.myapplication.utils.PreferencesManager

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefsManager = PreferencesManager(this)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // 1️⃣ FIRST TIME → ONBOARDING
        if (prefsManager.isFirstTime) {

            // ✅ FORCE RESET EVERYTHING (ADD ONLY THIS PART)
            FirebaseAuth.getInstance().signOut()

            getSharedPreferences("MyCafePrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            // ✅ KEEP YOUR EXISTING CODE
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }




        // 2️⃣ NOT LOGGED IN → LOGIN
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // 3️⃣ LOGGED IN → CHECK ROLE
        val sharedPrefs = getSharedPreferences("MyCafePrefs", MODE_PRIVATE)
        val role = sharedPrefs.getString("user_role", "user")

        if (role == "admin") {
            startActivity(Intent(this, AdminMainActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}
