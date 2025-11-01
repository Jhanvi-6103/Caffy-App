package com.myapp.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ViewholderItemListBinding
import java.text.NumberFormat
import java.util.Locale

class ItemListCategoryAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<ItemListCategoryAdapter.Viewholder>() {

    private lateinit var context: Context
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class Viewholder(val binding: ViewholderItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding =
            ViewholderItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items.getOrNull(position)

        holder.binding.titleTxt.text = item?.title ?: ""
        holder.binding.priceTxt.text = formatter.format(item?.price ?: 0.0)
        holder.binding.subtitleTxt.text = item?.extra ?: ""

        // Safe Glide loading
        val imageUrl = item?.picUrl?.firstOrNull()
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.pic)
        } else {
            holder.binding.pic.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", items[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
