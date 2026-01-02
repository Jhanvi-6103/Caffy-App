package com.myapp.myapplication.Helper

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.myapp.myapplication.Domain.ItemsModel

class WishlistManager(context: Context) {
    private val tinyDB: TinyDB = TinyDB(context)

    // UNIQUE key per user
    private val wishlistKey = "Wishlist_" + (FirebaseAuth.getInstance().uid ?: "guest")

    fun insertItem(item: ItemsModel) {
        val items = getList()
        if (items.none { it.title == item.title }) {
            items.add(item)
            tinyDB.putListObject(wishlistKey, items)
        }
    }

    fun removeItem(item: ItemsModel) {
        val items = getList()
        items.removeAll { it.title == item.title }
        tinyDB.putListObject(wishlistKey, items)
    }

    fun getList(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(wishlistKey) ?: ArrayList()
    }

    fun isWishlisted(title: String): Boolean {
        return getList().any { it.title == title }
    }
}
