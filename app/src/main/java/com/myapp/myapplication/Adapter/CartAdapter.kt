package com.myapp.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ChangeNumberItemsListener
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.databinding.ViewholderCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val listItemSelected: ArrayList<ItemsModel>,
    context: Context,
    var changeNumberItemListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val managmentCart = ManagmentCart(context)
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding =
            ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, @SuppressLint("RecyclerView") position: Int) {
        val item = listItemSelected[position]

        holder.binding.titleTxt.text = item.title
        holder.binding.feeEachItem.text = formatter.format(item.price)
        holder.binding.totalEachItem.text = formatter.format(item.numberInCart * item.price)
        holder.binding.numberInCartTxt.text = item.numberInCart.toString()

        // Show size in cart
        holder.binding.sizeTxt.text = "Size: ${item.size ?: "Not selected"}"

        Glide.with(holder.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.picCart)

        holder.binding.plusBtn.setOnClickListener {
            managmentCart.plusItem(listItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemListener?.onChanged()
                }
            })
        }

        holder.binding.minusBtn.setOnClickListener {
            managmentCart.minusItem(listItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemListener?.onChanged()
                }
            })
        }

        holder.binding.removeItemBtn.setOnClickListener {
            managmentCart.removeItem(listItemSelected, position, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemListener?.onChanged()
                }
            })
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}
