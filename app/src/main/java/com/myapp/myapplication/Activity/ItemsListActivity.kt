package com.myapp.myapplication.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.myapp.myapplication.Adapter.ItemListCategoryAdapter
import com.myapp.myapplication.R
import com.myapp.myapplication.ViewModel.MainViewModel
import com.myapp.myapplication.databinding.ActivityItemsListBinding
import com.myapp.myapplication.databinding.ViewholderItemListBinding

class ItemsListActivity : AppCompatActivity() {

    lateinit var binding: ActivityItemsListBinding
    private val viewModel=MainViewModel()

    private var id:String=""
    private var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityItemsListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getBundles()
        initList()

    }

    private fun initList() {
        binding.apply {
            progressBar3.visibility=View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemsListActivity, Observer {
                listView.layoutManager= GridLayoutManager(this@ItemsListActivity,2)

                listView.adapter= ItemListCategoryAdapter(it)
                progressBar3.visibility=View.GONE
            })
    backBtn.setOnClickListener{ finish()}
        }
    }

    private fun getBundles() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!

        binding.categoryTxt.text=title
    }
}