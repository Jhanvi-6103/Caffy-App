package com.myapp.myapplication.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityProceedOrderBinding

// ⭐ ADDED
import com.myapp.myapplication.Rewards.RewardManager

class ProceedOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProceedOrderBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var firebaseCartList: ArrayList<ItemsModel>

    // ⭐ ADDED
    private lateinit var rewardManager: RewardManager

    // Offer state
    private var availableOffers: MutableList<Pair<String, String>> = mutableListOf() // Pair<redeemKey, code>
    private var selectedRedeemKey: String? = null
    private var selectedOfferCode: String? = null
    private var discountAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProceedOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firebaseCartList = ArrayList()

        // ⭐ ADDED
        rewardManager = RewardManager(this)

        loadUserBasicDetails()
        loadCartFromFirebase()
        loadAvailableOffers()     // ⭐ load offers
        setupGenderDropdown()
        setupOffersSpinnerListener()

        binding.backBtn.setOnClickListener { finish() }

        binding.btnOrderNow.setOnClickListener { placeOrder() }


    }
    override fun onResume() {
        super.onResume()
        loadAvailableOffers()  // 🔥 refresh offer list every time
    }




    // ------------------- LOAD CART -------------------
    private fun loadCartFromFirebase() {
        val uid = auth.uid ?: return

        database.getReference("Cart").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseCartList.clear()

                    for (snap in snapshot.children) {
                        val item = snap.getValue(ItemsModel::class.java)
                        if (item != null) firebaseCartList.add(item)
                    }

                    // update preview of offer effect if needed
                    updateOfferPreview()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setupGenderDropdown() {
        val list = listOf("Select Gender", "Female", "Male", "Other")

        val adapter = ArrayAdapter(this, R.layout.spinner_item_custom, list)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spGender.adapter = adapter
    }

    private fun loadUserBasicDetails() {
        val uid = auth.uid ?: return

        database.getReference("users").child(uid).get()
            .addOnSuccessListener {
                binding.etName.setText(it.child("name").value?.toString() ?: "")
                binding.etEmail.setText(it.child("email").value?.toString() ?: "")
                binding.etPhone.setText(it.child("phone").value?.toString() ?: "")
            }
    }

    // ------------------- OFFERS -------------------
    private fun loadAvailableOffers() {
        val uid = auth.uid ?: return

        database.getReference("users")
            .child(uid)
            .child("rewards")
            .child("redeemed")
            .get()
            .addOnSuccessListener { snap ->

                availableOffers.clear()

                for (offerSnap in snap.children) {

                    val redeemKey = offerSnap.key ?: continue
                    val code = offerSnap.child("code").value?.toString() ?: ""
                    val used = offerSnap.child("used").getValue(Boolean::class.java) ?: false

                    // ⭐ ONLY OFFER IF NOT USED
                    if (!used && code.isNotEmpty()) {
                        availableOffers.add(Pair(redeemKey, code))
                    }
                }

                // ---------------------------
                // ⭐ BUILD SPINNER LIST
                // ---------------------------
                val spinnerList: MutableList<String> = mutableListOf()

                if (availableOffers.isEmpty()) {
                    spinnerList.add("No redeemed offers yet!")
                    selectedOfferCode = null
                    selectedRedeemKey = null
                } else {
                    spinnerList.add("Select offer")   // ⭐ default text
                    spinnerList.addAll(availableOffers.map { it.second })

                    selectedOfferCode = null
                    selectedRedeemKey = null
                }

                val adapter = ArrayAdapter(
                    this@ProceedOrderActivity,
                    android.R.layout.simple_spinner_item,
                    spinnerList
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spOffers.adapter = adapter

                updateOfferPreview()
            }
    }



    private fun setupOffersSpinnerListener() {
        binding.spOffers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (availableOffers.isEmpty()) {
                    selectedRedeemKey = null
                    selectedOfferCode = null
                } else {
                    if (position == 0) {
                        // ⭐ "Select offer"
                        selectedRedeemKey = null
                        selectedOfferCode = null
                    } else {
                        val pair = availableOffers[position - 1]
                        selectedRedeemKey = pair.first
                        selectedOfferCode = pair.second
                    }
                }
                updateOfferPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRedeemKey = null
                selectedOfferCode = null
                updateOfferPreview()
            }
        }
    }


    // Preview discount text on screen
    private fun updateOfferPreview() {
        var subtotal = 0.0
        for (item in firebaseCartList) subtotal += item.price * item.numberInCart
        val tax = subtotal * 0.02
        val delivery = 15.0
        val totalAmount = subtotal + tax + delivery

        val code = selectedOfferCode
        discountAmount = computeDiscountForCode(code, totalAmount)

        if (code != null && code.isNotEmpty() && discountAmount > 0) {
            binding.tvOfferInfo.text = "Offer: $code  •  Discount: ₹${"%.2f".format(discountAmount)}"
            binding.tvOfferInfo.visibility = android.view.View.VISIBLE
        } else if (code != null && code.isNotEmpty() && discountAmount == 0.0) {
            binding.tvOfferInfo.text = "Offer: $code"
            binding.tvOfferInfo.visibility = android.view.View.VISIBLE
        } else {
            binding.tvOfferInfo.visibility = android.view.View.GONE
        }
    }

    private fun computeDiscountForCode(code: String?, totalAmount: Double): Double {
        if (code.isNullOrBlank()) return 0.0

        val normalized = code.trim().uppercase()

        // -----------------------------
        // ⭐ OLD ENGINE (ONLY IF NEW LOGIC DID NOT MATCH)
        // -----------------------------
        val offRegex = Regex("""^(\d+)_?OFF$""", RegexOption.IGNORE_CASE)
        val flatRegex = Regex("""^FLAT(\d+)$""", RegexOption.IGNORE_CASE)
        val pctRegex = Regex("""^PCT[_-]?(\d+)$""", RegexOption.IGNORE_CASE)
        val pctAltRegex = Regex("""^(\d+)%$""")
        val freeDelRegex = Regex("""^FREEDEL|FREE_DELIVERY|FREEDELIVERY$""", RegexOption.IGNORE_CASE)

        offRegex.find(code)?.let {
            return it.groupValues[1].toDoubleOrNull() ?: 0.0
        }
        flatRegex.find(code)?.let {
            return it.groupValues[1].toDoubleOrNull() ?: 0.0
        }
        pctRegex.find(code)?.let {
            val v = it.groupValues[1].toDoubleOrNull() ?: 0.0
            return (totalAmount * v / 100.0)
        }
        pctAltRegex.find(code)?.let {
            val v = it.groupValues[1].toDoubleOrNull() ?: 0.0
            return (totalAmount * v / 100.0)
        }
        if (freeDelRegex.containsMatchIn(code)) {
            return 15.0
        }

        val digitsAtStart = Regex("""^(\d+).*""")
        digitsAtStart.find(code)?.let {
            return it.groupValues[1].toDoubleOrNull() ?: 0.0
        }

        return 0.0
    }


    // ------------------- PLACE ORDER -------------------
    private fun placeOrder() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val gender = binding.spGender.selectedItem.toString()
        val city = binding.etCity.text.toString().trim()
        val code = binding.etCityCode.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (name.isEmpty()) { binding.etName.error = "Enter name"; return }
        if (phone.length < 6) { binding.etPhone.error = "Invalid phone"; return }
        if (gender == "Select Gender") { toast("Select gender"); return }
        if (city.isEmpty()) { binding.etCity.error = "Enter city"; return }
        if (code.length < 3) { binding.etCityCode.error = "Invalid pincode"; return }
        if (address.length < 5) { binding.etAddress.error = "Enter address"; return }

        val uid = auth.uid!!
        val firstName = name.split(" ")[0]
        val randomNum = (1000..9999).random()
        val orderId = "$firstName-$randomNum"

        // ---------- Calculate Amount ----------
        var subtotal = 0.0
        for (item in firebaseCartList) {
            subtotal += item.price * item.numberInCart
        }
        val tax = subtotal * 0.02
        val delivery = 15.0
        val totalAmount = subtotal + tax + delivery

        // ---------- APPLY SELECTED OFFER ----------
        val offerCode = selectedOfferCode
        discountAmount = computeDiscountForCode(offerCode, totalAmount)
        var finalAmount = totalAmount - discountAmount
        if (finalAmount < 0.0) finalAmount = 0.0

        // ---------- Create Order Data ----------
        val orderDetails = hashMapOf(
            "orderId" to orderId,
            "userId" to uid,
            "userEmail" to binding.etEmail.text.toString(),
            "userName" to name,
            "phone" to phone,
            "gender" to gender,
            "city" to city,
            "pincode" to code,
            "address" to address,
            "timestamp" to System.currentTimeMillis(),
            "status" to "Pending",
            "totalAmount" to finalAmount,

            // pricing breakdown + applied offer (we include redeem key but DO NOT mark used here)
            "subtotal" to subtotal,
            "tax" to tax,
            "delivery" to delivery,
            "appliedOffer" to (offerCode ?: ""),
            "discountAmount" to discountAmount,
            "appliedRedeemKey" to (selectedRedeemKey ?: ""),

            // items
            "items" to firebaseCartList.map { it }
        )

        database.getReference("Orders").child(uid).child(orderId)
            .setValue(orderDetails)
            .addOnSuccessListener {
                // IMPORTANT: do NOT mark offer used here. We mark used only when order becomes Paid (handled in OrderSummaryActivity).
                // Update rewards/achievements using finalAmount (after discount)
                rewardManager.updateAfterOrder(uid, finalAmount) {
                    // optional logging
                }

                // update user profile
                database.getReference("users").child(uid).updateChildren(
                    mapOf(
                        "name" to name,
                        "phone" to phone,
                        "gender" to gender,
                        "city" to city,
                        "pincode" to code,
                        "address" to address
                    )
                )

                // CLEAR CART
                database.getReference("Cart").child(uid).removeValue()

                toast("Order placed successfully!")

                val intent = Intent(this, OrderSummaryActivity::class.java)
                intent.putExtra("orderId", orderId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                toast("Error placing order")
            }

        // ⭐ MARK REDEEMED OFFER AS USED IMMEDIATELY
        if (!selectedRedeemKey.isNullOrEmpty()) {
            FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("rewards")
                .child("redeemed")
                .child(selectedRedeemKey!!)
                .child("used")
                .setValue(true)
        }

    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
