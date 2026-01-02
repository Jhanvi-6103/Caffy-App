package com.myapp.myapplication.Adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ViewholderItemListBinding
import kotlin.random.Random

class ItemListCategoryAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<ItemListCategoryAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var running = true    // control typing loop
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding =
            ViewholderItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]
        holder.running = true

        holder.binding.apply {

            // Title & subtitle
            titleTxt.text = item.title
            subtitleTxt.text = item.extra

            // Price text
            val smallPrice = item.sizePrice?.small ?: item.price
            val finalText = "Prices from ₹$smallPrice"

            priceTxt.setTextColor(ContextCompat.getColor(context, R.color.brown))
            priceTxt.text = "Loading..."
            priceTxt.alpha = 0.4f

            // Start typing animation after small delay
            priceTxt.postDelayed({
                if (holder.running) animateTyping(finalText, holder)
            }, 80)

            // Load image
            Glide.with(context)
                .load(item.picUrl.firstOrNull())
                .into(pic)

            // Card hover animation
            rootCard.setOnTouchListener { view, event ->
                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        val upX = PropertyValuesHolder.ofFloat("scaleX", 1.04f)
                        val upY = PropertyValuesHolder.ofFloat("scaleY", 1.04f)
                        ObjectAnimator.ofPropertyValuesHolder(view, upX, upY)
                            .setDuration(120).start()
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        val downX = PropertyValuesHolder.ofFloat("scaleX", 1f)
                        val downY = PropertyValuesHolder.ofFloat("scaleY", 1f)
                        ObjectAnimator.ofPropertyValuesHolder(view, downX, downY)
                            .setDuration(120).start()
                    }
                }
                false
            }

            // Go to details page
            rootCard.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }

            // (Optional) Add button click - opens details too
            addBtn.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }
        }
    }

    // -----------------------------------------
    // ⭐ Smooth Typing Animation + Fade-out Loop
    // -----------------------------------------
    private fun animateTyping(text: String, holder: Viewholder) {

        val priceTxt = holder.binding.priceTxt
        priceTxt.text = ""
        priceTxt.alpha = 1f

        var i = 0

        val typingRunnable = object : Runnable {
            override fun run() {

                if (!holder.running) return  // stop if recycled

                if (i <= text.length) {
                    priceTxt.text = text.substring(0, i)
                    i++

                    // Slow luxury typing rhythm
                    val delay = Random.nextLong(120, 200)

                    priceTxt.postDelayed(this, delay)

                } else {
                    // Fade out before restart
                    priceTxt.animate()
                        .alpha(0f)
                        .setDuration(450)
                        .withEndAction {
                            if (holder.running) {
                                priceTxt.postDelayed({
                                    animateTyping(text, holder)
                                }, 350)
                            }
                        }
                        .start()
                }
            }
        }

        priceTxt.post(typingRunnable)
    }

    // -----------------------------------------
    // STOP animation when off-screen
    // -----------------------------------------
    override fun onViewRecycled(holder: Viewholder) {
        super.onViewRecycled(holder)
        holder.running = false
        holder.binding.priceTxt.removeCallbacks(null)
    }

    override fun getItemCount(): Int = items.size
}
