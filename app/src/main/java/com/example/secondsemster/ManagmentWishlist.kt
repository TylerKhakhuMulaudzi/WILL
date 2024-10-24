package com.example.secondsemster

import android.content.Context
import android.widget.Toast

class  ManagmentWishlist(private val context: Context) {
    private val tinyDB = TinyDB(context)
    private val WISHLIST_KEY = "wishlistItems"

    fun addToWishlist(item: ItemsModel) {
        val wishlist = getWishlist()
        if (!wishlist.any { it.title == item.title }) {
            wishlist.add(item)
            tinyDB.putListObject(WISHLIST_KEY, wishlist)
            Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Item already in wishlist", Toast.LENGTH_SHORT).show()
        }
    }

    fun getWishlist(): ArrayList<ItemsModel> {
        return tinyDB.getListObject(WISHLIST_KEY) ?: arrayListOf()
    }

    fun removeFromWishlist(position: Int) {
        val wishlist = getWishlist()
        wishlist.removeAt(position)
        tinyDB.putListObject(WISHLIST_KEY, wishlist)
        Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show()
    }

    fun isInWishlist(item: ItemsModel): Boolean {
        return getWishlist().any { it.title == item.title }
    }

    fun clearWishlist() {
        tinyDB.putListObject(WISHLIST_KEY, arrayListOf<ItemsModel>())
    }
}