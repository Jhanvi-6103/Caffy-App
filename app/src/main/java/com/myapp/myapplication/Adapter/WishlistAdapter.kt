package com.myapp.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ViewholderWishlistBinding
import java.util.Locale

class WishlistAdapter(
    private val items: ArrayList<ItemsModel>,
    private val context: Context,
    private val onDeleteClick: (ItemsModel) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderWishlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.titleTxt.text = item.title

        // ⭐ Format price with two decimals
        val formattedPrice = String.format(Locale.US, "%.2f", item.price.toDouble())
        holder.binding.priceTxt.text = "₹$formattedPrice"

        Glide.with(context)
            .load(item.picUrl[0])
            .into(holder.binding.pic)

        // ---------------------------------------------
        // ⭐ DELETE BUTTON
        // ---------------------------------------------
        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(item)
        }

        // ---------------------------------------------
        // ⭐ OPEN DETAILS PAGE
        // ---------------------------------------------
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderWishlistBinding) :
        RecyclerView.ViewHolder(binding.root)
}
