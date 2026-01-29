package com.myapp.myapplication.Admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.myapp.myapplication.R
import com.myapp.myapplication.Admin.fragments.AdminHomeFragment
import com.myapp.myapplication.Admin.fragments.AdminRequestsFragment
import com.myapp.myapplication.Admin.fragments.AdminPaymentsFragment
import com.myapp.myapplication.Admin.fragments.AdminCancelledFragment
import com.myapp.myapplication.Admin.fragments.AdminTotalOrdersFragment

class AdminMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        // Load default fragment
        loadFragment(AdminHomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.adminBottomNav)

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_admin_home -> loadFragment(AdminHomeFragment())
                R.id.menu_admin_requests -> loadFragment(AdminRequestsFragment())
                R.id.menu_admin_payments -> loadFragment(AdminPaymentsFragment())
                R.id.menu_admin_cancelled -> loadFragment(AdminCancelledFragment())
                R.id.menu_admin_all_orders -> loadFragment(AdminTotalOrdersFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .commit()
    }
}
