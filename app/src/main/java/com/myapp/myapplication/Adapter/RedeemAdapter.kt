package com.myapp.myapplication.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Rewards.CoinsFragment
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ItemRedeemBinding

class RedeemAdapter(
    private val items: List<CoinsFragment.RewardOption>,
    private val usedCodes: Set<String>,     // ⭐ Set of redeemed codes
    private val onClick: (CoinsFragment.RewardOption) -> Unit
) : RecyclerView.Adapter<RedeemAdapter.VH>() {

    inner class VH(val binding: ItemRedeemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRedeemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val isRedeemed = usedCodes.contains(item.code) // ⭐ check if this offer is already redeemed

        holder.binding.apply {

            // Normal text
            title.text = item.label
            cost.text = "${item.cost} coins"

            if (isRedeemed) {
                // 🔥 DISABLE CARD VISUALLY
                root.alpha = 0.6f
                root.isClickable = false

                // 🔥 Text grey
                title.setTextColor(Color.GRAY)
                cost.setTextColor(Color.GRAY)

                // 🔥 Show tick
                tick.visibility = View.VISIBLE

                // 🔥 Hide arrow
                arrow.visibility = View.GONE

            } else {
                // Normal state
                root.alpha = 1f
                root.isClickable = true
                title.setTextColor(ContextCompat.getColor(root.context, R.color.darkBrown))
                cost.setTextColor(ContextCompat.getColor(root.context, R.color.cafe_accent))

                tick.visibility = View.GONE
                arrow.visibility = View.VISIBLE

                // Clickable
                root.setOnClickListener {
                    root.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction {
                        root.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }.start()
                    onClick(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
