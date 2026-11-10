package com.example.coffee.Helper

import android.content.Context
import android.widget.Toast
import com.example.coffee.domain.ItemModel
import com.google.firebase.database.FirebaseDatabase

class ManagmentWishlist(val context: Context) {
    private val tinyDB = TinyDB(context)
    private val database = FirebaseDatabase.getInstance()

    fun getWishlist(): ArrayList<ItemModel> {
        return tinyDB.getListObject("Wishlist") ?: arrayListOf()
    }

    fun isWishlisted(item: ItemModel): Boolean {
        val list = getWishlist()
        return list.any { it.title.equals(item.title, ignoreCase = true) }
    }

    fun toggleWishlist(item: ItemModel): Boolean {
        val list = getWishlist()
        val exists = list.any { it.title.equals(item.title, ignoreCase = true) }
        val isNowWishlisted = if (exists) {
            list.removeAll { it.title.equals(item.title, ignoreCase = true) }
            Toast.makeText(context, "${item.title} removed from WishList", Toast.LENGTH_SHORT).show()
            false
        } else {
            list.add(item)
            Toast.makeText(context, "${item.title} added to WishList! ❤️", Toast.LENGTH_SHORT).show()
            true
        }
        tinyDB.putListObject("Wishlist", list)
        syncWithCloud(list)
        return isNowWishlisted
    }

    fun removeFromWishlist(item: ItemModel, onRemoved: (() -> Unit)? = null) {
        val list = getWishlist()
        list.removeAll { it.title.equals(item.title, ignoreCase = true) }
        tinyDB.putListObject("Wishlist", list)
        syncWithCloud(list)
        Toast.makeText(context, "${item.title} removed from WishList", Toast.LENGTH_SHORT).show()
        onRemoved?.invoke()
    }

    private fun syncWithCloud(list: ArrayList<ItemModel>) {
        val email = tinyDB.getString("user_email")
        if (email.isNotEmpty()) {
            val userKey = email.trim().lowercase().replace(".", "_").replace("@", "_at_")
            database.getReference("Users").child(userKey).child("wishlist").setValue(list)
        }
    }
}
