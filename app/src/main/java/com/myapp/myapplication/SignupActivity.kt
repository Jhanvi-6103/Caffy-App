package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import android.text.InputFilter
import android.util.Patterns
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.myapp.myapplication.R
import com.myapp.myapplication.Rewards.RewardManager
import com.myapp.myapplication.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleRequest: BeginSignInRequest

    // Reward Manager
    private lateinit var rewardManager: RewardManager

    private val GOOGLE_SIGNUP = 1001

    private val phoneLengthMap = mapOf(
        "+91" to 10, "+1" to 10, "+44" to 10, "+61" to 9, "+81" to 10,
        "+92" to 10, "+977" to 10, "+971" to 9
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔽 ADD THIS BLOCK HERE 🔽
        loadSignupGif()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        rewardManager = RewardManager(this)   // ⭐ INIT

        setupCountryCodes()
        setupPhoneLengthListener()
        setupPasswordToggle()
        setupGoogleSignup()

        binding.btnSignup.setOnClickListener { registerUser() }
        binding.btnGoogleSignup.setOnClickListener { startGoogleSignup() }

        binding.tvLoginClick.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun loadSignupGif() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.signupgif)   // your GIF file
            .into(binding.imgSignupGif)

        // Optional fade-in animation
        binding.imgSignupGif.alpha = 0f
        binding.imgSignupGif.animate()
            .alpha(1f)
            .setDuration(800)
            .start()
    }


    // COUNTRY CODE HANDLING
    // -------------------------------------------------------------
    private fun setupCountryCodes() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_country, phoneLengthMap.keys.toList())
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_country)
        binding.spCountryCode.adapter = adapter
    }

    private fun setupPhoneLengthListener() {
        binding.spCountryCode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val code = binding.spCountryCode.selectedItem.toString()
                    val maxLen = phoneLengthMap[code] ?: 10

                    binding.etPhone.text?.clear()
                    binding.etPhone.filters = arrayOf(InputFilter.LengthFilter(maxLen))
                    binding.etPhone.hint = "Phone Number ($maxLen digits)"
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }
    // PASSWORD TOGGLE
    // -------------------------------------------------------------
    private fun setupPasswordToggle() {
        var visible = false

        binding.ivTogglePassword.setOnClickListener {
            visible = !visible

            if (visible) {
                binding.etPassword.inputType =
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_show)
            } else {
                binding.etPassword.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_hide)
            }

            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }

    // GOOGLE SIGN UP
    // -------------------------------------------------------------
    private fun setupGoogleSignup() {
        googleRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()
    }

    private fun startGoogleSignup() {
        Identity.getSignInClient(this).beginSignIn(googleRequest)
            .addOnSuccessListener {
                startIntentSenderForResult(
                    it.pendingIntent.intentSender,
                    GOOGLE_SIGNUP,
                    null, 0, 0, 0, null
                )
            }.addOnFailureListener {
                toast("Google signup failed")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGNUP) {

            val credential = Identity.getSignInClient(this)
                .getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken ?: return

            auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnSuccessListener {

                    val user = auth.currentUser!!
                    val ref = database.getReference("users").child(user.uid)

                    val map = mapOf(
                        "name" to (user.displayName ?: "User"),
                        "email" to (user.email ?: ""),
                        "phone" to "",
                        "countryCode" to ""
                    )

                    ref.setValue(map)

                    // ⭐ Initialize Rewards
                    rewardManager.initializeRewardsIfMissing(user.uid)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { toast(it.message ?: "Error") }
        }
    }

    // -------------------------------------------------------------
    // EMAIL SIGN UP
    // -------------------------------------------------------------
    private fun registerUser() {

        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val code = binding.spCountryCode.selectedItem.toString()
        val phone = binding.etPhone.text.toString().trim()

        val requiredLen = phoneLengthMap[code] ?: 10

        if (name.length < 3) { binding.etName.error = "Invalid name"; return }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email"; return
        }
        if (password.length < 6) { binding.etPassword.error = "Weak password"; return }
        if (phone.length != requiredLen) {
            binding.etPhone.error = "Must be $requiredLen digits"; return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser!!.uid
                val ref = database.getReference("users").child(uid)

                val map = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "countryCode" to code
                )

                ref.setValue(map)

                // ⭐ Initialize Rewards
                rewardManager.initializeRewardsIfMissing(uid)

                toast("Signup Successful!")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { toast(it.message ?: "Error") }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
