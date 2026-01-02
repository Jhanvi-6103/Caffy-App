package com.myapp.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R

class RecommendedItemsAdapter(
    private val items: List<ItemsModel>
) : RecyclerView.Adapter<RecommendedItemsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtItemTitle)
        val price: TextView = view.findViewById(R.id.txtItemPrice)
        val orderAgain: Button = view.findViewById(R.id.btnOrderAgain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommended, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.title.text = item.title
        holder.price.text = "₹${item.getPriceBySelectedSize()}"

        holder.orderAgain.setOnClickListener {
            ManagmentCart(holder.itemView.context).insertItems(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
