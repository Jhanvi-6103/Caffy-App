package com.myapp.myapplication.Activity // Your package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils // <-- 1. IMPORT THIS
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Domain.OnboardingItem // Your package for the data class
import com.myapp.myapplication.R

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 2. Make these public (remove 'private')
        val image: ImageView = view.findViewById(R.id.onboardingImage)
        val title: TextView = view.findViewById(R.id.onboardingTitle)
        val description: TextView = view.findViewById(R.id.onboardingDescription)

        fun bind(item: OnboardingItem) {
            image.setImageResource(item.imageRes)
            title.text = item.title
            description.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_onboarding_page,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 3. ADD THIS FUNCTION TO START ANIMATIONS
    override fun onViewAttachedToWindow(holder: OnboardingViewHolder) {
        super.onViewAttachedToWindow(holder)

        // Load animations
        val animFadeIn = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        val animSlideUp = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)

        // Start animations
        holder.image.startAnimation(animFadeIn)
        holder.title.startAnimation(animSlideUp)

        // Create a staggered effect for the description
        val animSlideUpDelayed = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
        animSlideUpDelayed.startOffset = 200 // 200ms delay
        holder.description.startAnimation(animSlideUpDelayed)
    }

    // 4. ADD THIS FUNCTION TO CLEAR ANIMATIONS
    override fun onViewDetachedFromWindow(holder: OnboardingViewHolder) {
        super.onViewDetachedFromWindow(holder)

        // Clear animations when the view is detached
        // This prevents them from re-playing weirdly when scrolling back
        holder.image.clearAnimation()
        holder.title.clearAnimation()
        holder.description.clearAnimation()
    }
}