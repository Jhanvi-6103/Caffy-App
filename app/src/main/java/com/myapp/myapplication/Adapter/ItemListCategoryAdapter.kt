package com.myapp.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ViewholderCategoryBinding
import com.myapp.myapplication.databinding.ViewholderItemListBinding
import com.myapp.myapplication.databinding.ViewholderPopularBinding

class ItemListCategoryAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<ItemListCategoryAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListCategoryAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ItemListCategoryAdapter.Viewholder, position: Int) {
        val item = items.getOrNull(position)

        holder.binding.titleTxt.text = item?.title ?: ""
        holder.binding.priceTxt.text = "$" + (item?.price ?: 0.0).toString()
        holder.binding.subtitleTxt.text = item?.extra ?: ""

        // Safe Glide loading
        val picList = item?.picUrl
        val imageUrl = picList?.firstOrNull() // safely get first item or null
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.pic)
        } else {
            // Clear ImageView if no image exists
            holder.binding.pic.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            val intent= Intent (context, DetailActivity::class.java)
            intent.putExtra("object",items[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
