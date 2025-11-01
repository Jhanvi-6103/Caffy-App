package com.myapp.myapplication.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapp.myapplication.Activity.DetailActivity
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.databinding.ViewholderPopularBinding
import java.text.NumberFormat
import java.util.Locale

class PopularAdapter(val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    class Viewholder(val binding: ViewholderPopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.Viewholder, position: Int) {
        val item = items.getOrNull(position)

        holder.binding.titleTxt.text = item?.title ?: ""
        // Format price in Indian Rupees
        holder.binding.priceTxt.text = if (item != null) formatter.format(item.price) else "₹0"
        holder.binding.subtitleTxt.text = item?.extra ?: ""

        val picList = item?.picUrl
        if (!picList.isNullOrEmpty() && picList[0].isNotEmpty()) {
            Glide.with(context)
                .load(picList[0])
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
