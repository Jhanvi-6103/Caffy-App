package com.myapp.myapplication.Adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ViewholderPopularBinding
import kotlin.random.Random

class PopularAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        return Viewholder(
            ViewholderPopularBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        val item = items[position]
        val binding = holder.binding

        // Title & Subtitle
        binding.titleTxt.text = item.title
        binding.subtitleTxt.text = item.extra

        // Small price
        val smallPrice = item.sizePrice?.small ?: item.price
        val finalText = "Prices from ₹$smallPrice"

        // Set color
        binding.priceTxt.setTextColor(ContextCompat.getColor(context, R.color.white))

        // Placeholder
        binding.priceTxt.text = "Loading..."
        binding.priceTxt.alpha = 0.5f

        // Start typing after view is drawn
        binding.priceTxt.postDelayed({
            animateTyping(finalText, holder)
        }, 100)

        // Load image
        Glide.with(context)
            .load(item.picUrl.firstOrNull())
            .into(binding.pic)

        // Open detail
        binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    // ---------------------------------------------------------
    //  ⭐ Typing + Fade Out Animation (Smooth + Slow)
    // ---------------------------------------------------------
    private fun animateTyping(text: String, holder: Viewholder) {

        val priceTxt = holder.binding.priceTxt
        priceTxt.text = ""
        priceTxt.alpha = 1f
        var i = 0

        val runnable = object : Runnable {
            override fun run() {
                if (i <= text.length) {
                    priceTxt.text = text.substring(0, i)
                    i++

                    // Slow typing rhythm
                    val delay = Random.nextLong(120, 200)
                    priceTxt.postDelayed(this, delay)

                } else {
                    // Fade-out effect
                    priceTxt.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction {
                            priceTxt.postDelayed({
                                animateTyping(text, holder)
                            }, 400)
                        }
                        .start()
                }
            }
        }

        priceTxt.post(runnable)
    }

    override fun getItemCount(): Int = items.size
}
