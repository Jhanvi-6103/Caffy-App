package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.databinding.LoginActivityBinding

// ⭐ Rewards
import com.myapp.myapplication.Rewards.RewardManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var rewardManager: RewardManager   // ⭐ ADDED

    private val oneTapClient by lazy {
        Identity.getSignInClient(this)
    }

    private lateinit var signInRequest: BeginSignInRequest

    // Google login launcher
    private val googleLoginLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)

                    auth.signInWithCredential(firebaseCred)
                        .addOnSuccessListener {

                            val user = auth.currentUser
                            if (user != null) {
                                val uid = user.uid
                                val userRef = database.getReference("users").child(uid)

                                val map = HashMap<String, Any>()
                                map["name"] = user.displayName ?: ""
                                map["email"] = user.email ?: ""
                                map["imageUrl"] = user.photoUrl?.toString() ?: ""

                                // Save only if user data doesn't exist
                                userRef.get().addOnSuccessListener { snapshot ->
                                    if (!snapshot.exists()) {
                                        userRef.setValue(map)
                                    }
                                }

                                // ⭐ Initialize Rewards
                                rewardManager.initializeRewardsIfMissing(uid)
                            }

                            Toast.makeText(this, "Google Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        }
                }

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -----------------------------------------------------------------
    // onCreate
    // -----------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        rewardManager = RewardManager(this)   // ⭐ INIT

        setupGoogleLogin()

        binding.btnGoogleLogin.setOnClickListener { startGoogleLogin() }
        binding.btnLogin.setOnClickListener { loginUser() }
        binding.tvSignupClick.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    // -----------------------------------------------------------------
    // GOOGLE LOGIN SETUP
    // -----------------------------------------------------------------
    private fun setupGoogleLogin() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("275416834471-56mq9tsir5spe7bputjvvr511l3on4nk.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    private fun startGoogleLogin() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                googleLoginLauncher.launch(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                )
            }
            .addOnFailureListener {
                Toast.makeText(this, "Google Sign-in failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // -----------------------------------------------------------------
    // NORMAL EMAIL LOGIN
    // -----------------------------------------------------------------
    private fun loginUser() {

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter valid email"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser!!.uid
                val userRef = database.getReference("users").child(uid)

                // Create minimal user profile if missing
                userRef.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {

                        val map = HashMap<String, Any>()
                        map["name"] = ""
                        map["email"] = email
                        map["imageUrl"] = ""

                        userRef.setValue(map)
                    }
                }

                // ⭐ Initialize Rewards
                rewardManager.initializeRewardsIfMissing(uid)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
