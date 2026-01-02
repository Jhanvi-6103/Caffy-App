package com.myapp.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Rewards.AchievementsFragment
import com.myapp.myapplication.databinding.ItemAchievementBinding

class AchievementsAdapter(
    private val items: MutableList<AchievementsFragment.AchievementItem>
) : RecyclerView.Adapter<AchievementsAdapter.VH>() {

    inner class VH(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.binding.apply {
            title.text = item.title
            desc.text = item.description

            // Visual state update
            if (item.unlocked) {
                lockedIndicator.text = "Unlocked"
                lockedIndicator.alpha = 1f
                root.alpha = 1f
            } else {
                lockedIndicator.text = "Locked"
                lockedIndicator.alpha = 0.5f
                root.alpha = 0.5f
            }

            // Small fade animation for smooth update
            root.animate().alpha(root.alpha).setDuration(250).start()
        }
    }

    override fun getItemCount(): Int = items.size
}
