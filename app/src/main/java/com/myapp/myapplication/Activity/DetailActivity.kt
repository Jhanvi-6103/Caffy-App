package com.myapp.myapplication.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Helper.ManagmentCart
import com.myapp.myapplication.R
import com.myapp.myapplication.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    lateinit var binding:ActivityDetailBinding
    private lateinit var item:ItemsModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart= ManagmentCart(this)

        bundle()
        initSizeList()
    }


    private fun initSizeList() {
        binding.apply {

            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }
            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
                largeBtn.setBackgroundResource(0)
            }

            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.brown_stroke_bg)
            }

        }
    }

    private fun bundle() {
        binding.apply {
            item=intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(picMain)
            // // Safe Glide loading
            //        val picList = item?.picUrl
            //        val imageUrl = picList?.firstOrNull() // safely get first item or null
            //        if (!imageUrl.isNullOrEmpty()) {
            //            Glide.with(context)
            //                .load(imageUrl)
            //                .into(holder.binding.pic)
            //        } else {
            //            // Clear ImageView if no image exists
            //            holder.binding.pic.setImageDrawable(null)
            //        }


            titleTxt.text=item.title
            descriptionTxt.text=item.description
            priceTxt.text="$"+item.price
            ratingTxt.text=item.rating.toString()


            addToCartBtn.setOnClickListener{
                item.numberInCart=Integer.valueOf(
                numberInCartTxt.text.toString()
                )
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener{ finish() }

            plusBtn.setOnClickListener{
                numberInCartTxt.text=(item.numberInCart+1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener {
                if(item.numberInCart>0){
                    numberInCartTxt.text=(item.numberInCart-1).toString()
                    item.numberInCart--
                }
            }

        }
    }
}