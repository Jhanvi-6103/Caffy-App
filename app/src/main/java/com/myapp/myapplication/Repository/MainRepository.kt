package com.myapp.myapplication.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.myapp.myapplication.Domain.BannerModel
import com.myapp.myapplication.Domain.CategoryModel
import com.myapp.myapplication.Domain.ItemsModel

class MainRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    // ---------------- EXISTING CODE (UNCHANGED) ----------------

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabase.getReference("Banner")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabase.getReference("Category")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Popular")

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
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    fun loadItemCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")

        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)

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
                itemsLiveData.value = mutableListOf()
            }
        })
        return itemsLiveData
    }

    fun getAllItems(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")

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
                listData.value = mutableListOf()
            }
        })
        return listData
    }

    // ---------------- 🔥 NEW RECOMMENDATION CODE ----------------

    fun loadRecommendedItems(userId: String): LiveData<MutableList<ItemsModel>> {

        val recommendedLiveData = MutableLiveData<MutableList<ItemsModel>>()

        val ordersRef = firebaseDatabase
            .getReference("Orders")
            .child(userId)

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val sizeCount = mutableMapOf<String, Int>()

                // Count sizes from previous orders
                for (order in snapshot.children) {
                    val size = order.child("size").getValue(String::class.java)
                    size?.let {
                        sizeCount[it] = sizeCount.getOrDefault(it, 0) + 1
                    }
                }

                // Default Medium if no orders
                val preferredSize =
                    sizeCount.maxByOrNull { it.value }?.key ?: "Medium"

                fetchItemsForRecommendation(preferredSize, recommendedLiveData)
            }

            override fun onCancelled(error: DatabaseError) {
                recommendedLiveData.value = mutableListOf()
            }
        })

        return recommendedLiveData
    }

    private fun fetchItemsForRecommendation(
        preferredSize: String,
        liveData: MutableLiveData<MutableList<ItemsModel>>
    ) {
        val itemsRef = firebaseDatabase.getReference("Items")

        itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        it.size = preferredSize
                        list.add(it)
                    }
                }

                liveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.value = mutableListOf()
            }
        })
    }
}
