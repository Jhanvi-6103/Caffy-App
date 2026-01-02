package com.myapp.myapplication.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 🔙 Back Button
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Update Password Button
        binding.btnUpdatePassword.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {

        val oldPass = binding.etOldPass.text.toString().trim()
        val newPass = binding.etNewPass.text.toString().trim()
        val confirmPass = binding.etConfirmPass.text.toString().trim()

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser!!
        val email = user.email!!

        val credential = EmailAuthProvider.getCredential(email, oldPass)

        // Re-authenticate before password change
        user.reauthenticate(credential)
            .addOnSuccessListener {

                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener {
                Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show()
            }
    }
}
