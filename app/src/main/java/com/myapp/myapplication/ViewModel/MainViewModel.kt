package com.myapp.myapplication.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.myapp.myapplication.Domain.BannerModel
import com.myapp.myapplication.Domain.CategoryModel
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Repository.MainRepository

class MainViewModel:ViewModel() {

    private val respository=MainRepository()

    fun loadBanner(): LiveData<MutableList<BannerModel>>{
        return respository.loadBanner()
    }
    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        return respository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        return respository.loadPopular()
    }

    fun loadItems(categoryId:String): LiveData<MutableList<ItemsModel>>{
        return respository.loadItemCategory(categoryId)
    }

}