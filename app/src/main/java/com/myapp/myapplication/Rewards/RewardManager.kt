package com.myapp.myapplication.Rewards

import android.content.Context
import com.google.firebase.database.*
import java.security.MessageDigest
import java.util.Locale
import kotlin.math.roundToLong

class RewardManager(private val context: Context) {

    companion object {
        private const val SECRET_KEY =
            "9fG$2K!e@1LpQx74Zb#Vu!s8TrM0^dWi#Pz8Y$@qN2lP!7cF@Qm^4Rt&X9yUzB"
        private const val PATH = "users"
    }

    private val db = FirebaseDatabase.getInstance().reference

    data class RewardsSnapshot(
        val coins: Int = 0,
        val orders: Int = 0,
        val spent: Double = 0.0,
        val achievements: Map<String, Boolean> = emptyMap()
    )

    // ⭐ NEW – Redeemed item model
    data class RedeemedItem(
        val key: String = "",
        val code: String = "",
        val cost: Int = 0,
        val used: Boolean = false,
        val createdAt: Long = 0L
    )

    // ---------------- HASH HELPERS ----------------
    private fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun calcHash(coins: Int, orders: Int, spent: Double): String {
        val spentStr = String.format(Locale.US, "%.0f", spent)
        return sha256Hex("$coins|$orders|$spentStr|$SECRET_KEY")
    }

    // ---------------- INIT ----------------
    fun initializeRewardsIfMissing(uid: String) {
        val ref = db.child(PATH).child(uid).child("rewards")

        ref.get().addOnSuccessListener { snap ->
            val alreadyInit =
                snap.child("coins").exists() ||
                        snap.child("securityHash").exists() ||
                        snap.child("stats").exists()

            if (alreadyInit) return@addOnSuccessListener

            val coins = 0
            val orders = 0
            val spent = 0.0
            val hash = calcHash(coins, orders, spent)

            val map = mapOf(
                "coins" to coins,
                "securityHash" to hash,
                "stats" to mapOf(
                    "total_orders" to orders,
                    "total_spent" to spent
                ),
                "achievements" to mapOf(
                    "first_order" to false,
                    "five_orders" to false,
                    "spent_500" to false
                )
            )

            ref.setValue(map)
        }
    }

    // ---------------- READ REWARDS ----------------
    fun readRewardsOnce(uid: String, callback: (Boolean, RewardsSnapshot) -> Unit) {
        db.child(PATH).child(uid).child("rewards")
            .get()
            .addOnSuccessListener { snap ->
                try {
                    val coins = snap.child("coins").getValue(Int::class.java) ?: 0
                    val orders = snap.child("stats/total_orders").getValue(Int::class.java) ?: 0
                    val spent = snap.child("stats/total_spent").getValue(Double::class.java) ?: 0.0

                    val achievements = snap.child("achievements").children.associate {
                        it.key!! to (it.value as? Boolean ?: false)
                    }

                    val storedHash = snap.child("securityHash").value?.toString() ?: ""
                    val valid = storedHash == calcHash(coins, orders, spent)

                    callback(valid, RewardsSnapshot(coins, orders, spent, achievements))
                } catch (e: Exception) {
                    callback(false, RewardsSnapshot())
                }
            }
            .addOnFailureListener {
                callback(false, RewardsSnapshot())
            }
    }

    // ---------------- ⭐ NEW — READ REDEEMED OFFERS ----------------
    fun readRedeemed(uid: String, callback: (List<RedeemedItem>) -> Unit) {
        db.child(PATH).child(uid).child("rewards").child("redeemed")
            .get()
            .addOnSuccessListener { snap ->

                val list = mutableListOf<RedeemedItem>()

                for (child in snap.children) {
                    val key = child.key ?: continue

                    val code = child.child("code").value?.toString() ?: ""
                    val cost = child.child("cost").getValue(Int::class.java) ?: 0
                    val used = child.child("used").getValue(Boolean::class.java) ?: false
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L

                    if (code.isNotEmpty()) {
                        list.add(
                            RedeemedItem(
                                key = key,
                                code = code,
                                cost = cost,
                                used = used,
                                createdAt = createdAt
                            )
                        )
                    }
                }

                callback(list)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    // ---------------- AWARD COINS ----------------
    fun awardCoins(uid: String, amount: Int, onComplete: (Boolean) -> Unit) {
        if (amount <= 0) return onComplete(false)

        val ref = db.child(PATH).child(uid).child("rewards")

        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(current: MutableData): Transaction.Result {
                try {
                    val coins = current.child("coins").getValue(Int::class.java) ?: 0
                    val orders = current.child("stats/total_orders").getValue(Int::class.java) ?: 0
                    val spent = current.child("stats/total_spent").getValue(Double::class.java) ?: 0.0

                    val newCoins = coins + amount
                    val newHash = calcHash(newCoins, orders, spent)

                    current.child("coins").value = newCoins
                    current.child("securityHash").value = newHash
                } catch (e: Exception) {
                    return Transaction.abort()
                }
                return Transaction.success(current)
            }

            override fun onComplete(err: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                onComplete(err == null && committed)
            }
        })
    }

    // ---------------- REDEEM ----------------
    fun redeemReward(uid: String, cost: Int, code: String, onComplete: (Boolean, String) -> Unit) {
        if (cost <= 0) { onComplete(false, "Invalid cost"); return }

        val ref = db.child(PATH).child(uid).child("rewards")

        ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(current: MutableData): Transaction.Result {
                try {
                    val coins = current.child("coins").getValue(Int::class.java) ?: 0
                    val orders = current.child("stats/total_orders").getValue(Int::class.java) ?: 0
                    val spent = current.child("stats/total_spent").getValue(Double::class.java) ?: 0.0
                    val storedHash = current.child("securityHash").value?.toString() ?: ""

                    if (storedHash != calcHash(coins, orders, spent)) return Transaction.abort()
                    if (coins < cost) return Transaction.abort()

                    val newCoins = coins - cost
                    val newHash = calcHash(newCoins, orders, spent)

                    current.child("coins").value = newCoins
                    current.child("securityHash").value = newHash

                    val key = System.currentTimeMillis().toString()
                    val entry = mapOf(
                        "code" to code,
                        "cost" to cost,
                        "used" to false,
                        "createdAt" to System.currentTimeMillis()
                    )
                    current.child("redeemed").child(key).value = entry

                } catch (e: Exception) {
                    return Transaction.abort()
                }
                return Transaction.success(current)
            }

            override fun onComplete(err: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                if (err != null || !committed) onComplete(false, "Failed / Not enough coins")
                else onComplete(true, "Redeemed Successfully")
            }
        })
    }

    // ---------------- UPDATE AFTER ORDER ----------------
    fun updateAfterOrder(uid: String, orderTotal: Double, onComplete: (Boolean) -> Unit) {
        val ref = db.child(PATH).child(uid).child("rewards")

        ref.get().addOnSuccessListener { snap ->
            try {
                var coins = snap.child("coins").getValue(Int::class.java) ?: 0
                var orders = snap.child("stats/total_orders").getValue(Int::class.java) ?: 0
                var spent = snap.child("stats/total_spent").getValue(Double::class.java) ?: 0.0

                val achievements = snap.child("achievements").children.associate {
                    it.key!! to (it.value as? Boolean ?: false)
                }

                // update stats
                orders += 1
                spent = ((spent + orderTotal) * 100).roundToLong() / 100.0

                // base coins earned
                coins += (orderTotal.toInt() / 100) * 2

                val newAchievements = achievements.toMutableMap()

                if (orders >= 1 && achievements["first_order"] != true) {
                    coins += 5
                    newAchievements["first_order"] = true
                }
                if (orders >= 5 && achievements["five_orders"] != true) {
                    coins += 10
                    newAchievements["five_orders"] = true
                }
                if (spent >= 500 && achievements["spent_500"] != true) {
                    coins += 15
                    newAchievements["spent_500"] = true
                }

                val newHash = calcHash(coins, orders, spent)

                val updates = mapOf(
                    "coins" to coins,
                    "securityHash" to newHash,
                    "stats/total_orders" to orders,
                    "stats/total_spent" to spent,
                    "achievements" to newAchievements
                )

                ref.updateChildren(updates)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }

            } catch (e: Exception) {
                onComplete(false)
            }
        }.addOnFailureListener {
            onComplete(false)
        }
    }
}
