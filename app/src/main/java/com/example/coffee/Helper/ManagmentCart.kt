package com.example.coffee.Helper

import android.content.Context
import android.widget.Toast
import com.example.coffee.domain.ItemModel  // Corrected import

import kotlin.collections.indexOfFirst


class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItems(item: ItemModel) { // Changed ItemsModel to ItemModel
        var listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title && it.size == item.size }
        val index = listItem.indexOfFirst { it.title == item.title && it.size == item.size }

        if (existAlready) {
            listItem[index].numberInCart += item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemModel> { // Changed ItemsModel to ItemModel
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listItems: ArrayList<ItemModel>, position: Int, listener: ChangeNumberItemsListener) { // Changed ItemsModel to ItemModel
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }
    fun removeItem(listItems: ArrayList<ItemModel>, position: Int, listener: ChangeNumberItemsListener) { // Changed ItemsModel to ItemModel

        listItems.removeAt(position)

        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun plusItem(listItems: ArrayList<ItemModel>, position: Int, listener: ChangeNumberItemsListener) { // Changed ItemsModel to ItemModel
        listItems[position].numberInCart++
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    fun clearCart() {
        tinyDB.remove("CartList")
    }
}