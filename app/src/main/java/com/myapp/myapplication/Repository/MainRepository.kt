package com.myapp.myapplication.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.myapp.myapplication.Domain.BannerModel
import com.myapp.myapplication.Domain.CategoryModel
import com.myapp.myapplication.Domain.ItemsModel

class MainRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<BannerModel>>{
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref=firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list= mutableListOf<BannerModel>()

                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(BannerModel :: class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                // You should handle this, e.g., post an empty list
                listData.value = mutableListOf()
            }

        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref=firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list= mutableListOf<CategoryModel>()

                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(CategoryModel :: class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }

        })
        return listData
    }


    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref=firebaseDatabase.getReference("Popular")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list= mutableListOf<ItemsModel>()

                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(ItemsModel :: class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }

        })
        return listData
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId) // lowercase 'c'

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                itemsLiveData.value = mutableListOf() // return empty list on error
            }
        })

        return itemsLiveData
    }

    // --- 🚀 ADD THIS NEW FUNCTION ---
    // This function loads ALL items from the "Items" node
    fun getAllItems(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items") // <-- Gets from "Items"
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf() // Post empty list on error
            }

        })
        return listData
    }

}