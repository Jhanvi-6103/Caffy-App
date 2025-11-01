package com.myapp.myapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ViewholderWishlistBinding

class WishlistAdapter(
    private val items: ArrayList<ItemsModel>,
    private val context: Context,
    private val onDeleteClick: (ItemsModel) -> Unit // Listener for delete click
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderWishlistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleTxt.text = item.title
        holder.binding.priceTxt.text = "₹${item.price}"

        Glide.with(context)
            .load(item.picUrl[0])
            .into(holder.binding.pic)

        // Set the click listener for the delete button
        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderWishlistBinding) : RecyclerView.ViewHolder(binding.root)
}