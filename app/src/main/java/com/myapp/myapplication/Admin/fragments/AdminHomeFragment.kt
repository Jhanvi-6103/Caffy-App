package com.myapp.myapplication.Admin.fragments

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.database.*
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.FragmentAdminHomeBinding
import java.util.Calendar

class AdminHomeFragment : Fragment(R.layout.fragment_admin_home) {

    private lateinit var binding: FragmentAdminHomeBinding
    private val db = FirebaseDatabase.getInstance().reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdminHomeBinding.bind(view)
        loadDashboardStats()
        loadWeeklySalesChart()
        binding.profileIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.adminFragmentContainer, AdminProfileFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun loadDashboardStats() {
        db.child("Orders").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                var pending = 0
                var paid = 0
                var cancelled = 0
                var total = 0

                for (user in snapshot.children) {
                    for (orderSnap in user.children) {
                        total++
                        when (orderSnap.child("status").value.toString()) {
                            "Pending" -> pending++
                            "Paid", "Approved" -> paid++
                            "Cancelled", "Rejected" -> cancelled++
                        }
                    }
                }

                setCard(binding.cardPending, "Pending Orders", pending)
                setCard(binding.cardPaid, "Paid Orders", paid)
                setCard(binding.cardCancelled, "Cancelled Orders", cancelled)
                setCard(binding.cardTotal, "Total Orders", total)

                updateChart(pending, paid, cancelled, total)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun setCard(
        card: com.myapp.myapplication.databinding.ItemAdminCardBinding,
        label: String,
        value: Int
    ) {
        card.statLabelTxt.text = label
        animateCount(card.statCountTxt, value)
    }


    private fun animateCount(textView: TextView, end: Int) {
        val animator = ValueAnimator.ofInt(0, end)
        animator.duration = 800
        animator.addUpdateListener {
            textView.text = it.animatedValue.toString()
        }
        animator.start()
    }

//    private fun updateChart(pending: Int, paid: Int, cancelled: Int, total: Int) {
//        if (total == 0) {
//            binding.progressPending.progress = 0
//            binding.progressPaid.progress = 0
//            binding.progressCancelled.progress = 0
//            return
//        }
//
//        animateProgress(binding.progressPending, pending * 100 / total)
//        animateProgress(binding.progressPaid, paid * 100 / total)
//        animateProgress(binding.progressCancelled, cancelled * 100 / total)
//    }
private fun updateChart(pending: Int, paid: Int, cancelled: Int, total: Int) {

    if (total == 0) {
        binding.progressPending.progress = 0
        binding.progressPaid.progress = 0
        binding.progressCancelled.progress = 0

        binding.txtPendingPercent.text = "0%"
        binding.txtPaidPercent.text = "0%"
        binding.txtCancelledPercent.text = "0%"
        return
    }

    val pendingPercent = pending * 100 / total
    val paidPercent = paid * 100 / total
    val cancelledPercent = cancelled * 100 / total

    animateProgressWithText(
        binding.progressPending,
        binding.txtPendingPercent,
        pendingPercent
    )

    animateProgressWithText(
        binding.progressPaid,
        binding.txtPaidPercent,
        paidPercent
    )

    animateProgressWithText(
        binding.progressCancelled,
        binding.txtCancelledPercent,
        cancelledPercent
    )
}
    private fun animateProgressWithText(
        progressBar: ProgressBar,
        textView: TextView,
        value: Int
    ) {
        val animator = ValueAnimator.ofInt(0, value)
        animator.duration = 700
        animator.addUpdateListener {
            val v = it.animatedValue as Int
            progressBar.progress = v
            textView.text = "$v%"
        }
        animator.start()
    }


    private fun animateProgress(progressBar: ProgressBar, value: Int) {
        val animator = ValueAnimator.ofInt(0, value)
        animator.duration = 700
        animator.addUpdateListener {
            progressBar.progress = it.animatedValue as Int
        }
        animator.start()
    }
    private fun loadWeeklySalesChart() {

        val entries = ArrayList<BarEntry>()
        val dayTotals = HashMap<Int, Float>()

        db.child("Orders").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                dayTotals.clear()

                for (userSnap in snapshot.children) {
                    for (orderSnap in userSnap.children) {

                        val status = orderSnap.child("status")
                            .getValue(String::class.java)

                        if (status != "Paid") continue

                        val amount = orderSnap.child("totalAmount")
                            .getValue(Double::class.java) ?: 0.0

                        val time = orderSnap.child("timestamp")
                            .getValue(Long::class.java) ?: 0L

                        val cal = Calendar.getInstance()
                        cal.timeInMillis = time
                        val day = cal.get(Calendar.DAY_OF_WEEK)

                        dayTotals[day] =
                            (dayTotals[day] ?: 0f) + amount.toFloat()
                    }
                }

                entries.clear()
                for (i in 1..7) {
                    entries.add(
                        BarEntry(
                            (i - 1).toFloat(),
                            dayTotals[i] ?: 0f
                        )
                    )
                }

                val dataSet = BarDataSet(entries, "Weekly Sales")
                dataSet.color = Color.parseColor("#6F4E37")
                dataSet.valueTextSize = 10f

                val barData = BarData(dataSet)
                barData.barWidth = 0.6f

                binding.salesChart.apply {
                    data = barData
                    description.isEnabled = false
                    axisRight.isEnabled = false
                    axisLeft.axisMinimum = 0f
                    setFitBars(true)
                    animateY(800)
                    invalidate()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



}
