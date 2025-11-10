package com.myapp.myapplication.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.myapp.myapplication.Domain.BannerModel
import com.myapp.myapplication.Domain.CategoryModel
import com.myapp.myapplication.Domain.ItemsModel
import com.myapp.myapplication.Repository.MainRepository

class MainViewModel:ViewModel() {

    private val respository=MainRepository() // Note: 'respository' is misspelled, but we'll use it as-is.

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

    // --- ADD THIS NEW FUNCTION ---
    fun loadAllItems(): LiveData<MutableList<ItemsModel>>{
        // This will show an error until you do Step 2
        return respository.getAllItems()
    }

}