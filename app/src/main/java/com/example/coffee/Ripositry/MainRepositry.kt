package com.example.coffee.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coffee.domain.BannerModel
import com.example.coffee.domain.CatefgoryModel
import com.example.coffee.domain.ItemModel
import com.google.firebase.database.*

class MainRepository {

    private val database = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<List<BannerModel>> {
        val liveData = MutableLiveData<List<BannerModel>>()

        database.getReference("Banner")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    liveData.value = snapshot.children.mapNotNull {
                        it.getValue(BannerModel::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.value = emptyList()
                }
            })

        return liveData
    }

    fun loadCategory(): LiveData<List<CatefgoryModel>> {
        val liveData = MutableLiveData<List<CatefgoryModel>>()

        database.getReference("Category")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    liveData.value = snapshot.children.mapNotNull {
                        it.getValue(CatefgoryModel::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.value = emptyList()
                }
            })

        return liveData
    }

    fun loadPopular(): LiveData<List<ItemModel>> {
        val liveData = MutableLiveData<List<ItemModel>>()

        database.getReference("Popular")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    liveData.value = snapshot.children.mapNotNull {
                        it.getValue(ItemModel::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.value = emptyList()
                }
            })

        return liveData
    }

    fun loadItemCategory(categoryId: String): LiveData<List<ItemModel>> {
        val liveData = MutableLiveData<List<ItemModel>>()
        val ref = database.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemModel::class.java)
                    item?.let { list.add(it) }
                }
                liveData.value = list
            }
            override fun onCancelled(error: DatabaseError) {
                liveData.value = emptyList()
            }
        })

        return liveData
    }
}