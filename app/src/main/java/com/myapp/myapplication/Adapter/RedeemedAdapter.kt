package com.myapp.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Rewards.RedeemedFragment
import com.myapp.myapplication.databinding.ItemRedeemedBinding
import java.text.SimpleDateFormat
import java.util.*

class RedeemedAdapter(
    private val items: List<RedeemedFragment.RedeemedItem>
) : RecyclerView.Adapter<RedeemedAdapter.VH>() {

    inner class VH(val binding: ItemRedeemedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRedeemedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.binding.tvRewardName.text = item.rewardName
        holder.binding.tvCoinsUsed.text = "${item.coinsUsed} coins"

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(item.timestamp))
    }

    override fun getItemCount(): Int = items.size
}
