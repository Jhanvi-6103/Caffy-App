package com.myapp.myapplication.Helper

import android.content.Context
import com.myapp.myapplication.Domain.ItemsModel

class WishlistManager(context: Context) {
    private val tinyDB: TinyDB = TinyDB(context)
    private val wishlistKey = "Wishlist"

    fun insertItem(item: ItemsModel) {
        val items = getList()
        // Check if item is already in wishlist (using title as a unique ID)
        val itemExists = items.any { it.title == item.title }
        if (!itemExists) {
            items.add(item)
            tinyDB.putListObject(wishlistKey, items)
        }
    }

    fun removeItem(item: ItemsModel) {
        val items = getList()
        // Remove item based on title
        items.removeAll { it.title == item.title }
        tinyDB.putListObject(wishlistKey, items)
    }

    fun getList(): ArrayList<ItemsModel> {
        // Return the list of wishlisted items, or an empty list if none
        return tinyDB.getListObject(wishlistKey) ?: ArrayList()
    }

    fun isWishlisted(title: String): Boolean {
        val items = getList()
        // Check if an item with this title already exists
        return items.any { it.title == title }
    }
}