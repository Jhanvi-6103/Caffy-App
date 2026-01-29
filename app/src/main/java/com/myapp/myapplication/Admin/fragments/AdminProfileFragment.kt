package com.myapp.myapplication.Admin.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.Activity.LoginActivity
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment(R.layout.fragment_admin_profile) {

    private lateinit var binding: FragmentAdminProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminProfileBinding.bind(view)

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadAdminData()
        handleLogout()
    }

    private fun loadAdminData() {

        val user = auth.currentUser ?: return

        binding.apply {

            // ✅ DEFAULT ADMIN NAME
            adminNameTxt.text = "JCaffy"

            // ✅ EMAIL FROM AUTH
            adminEmailTxt.text = user.email ?: "admin@mycafe.com"

            // ✅ DEFAULT PHONE (DEMO)
            adminPhoneTxt.text = "Phone: +91 84900 56103"

            // ✅ ROLE
            adminRoleTxt.text = "Role: Admin"
        }
    }

    private fun handleLogout() {
        binding.logoutBtn.setOnClickListener {
            auth.signOut()

            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}
