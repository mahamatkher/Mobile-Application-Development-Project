package com.example.coffee.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.coffee.domain.BannerModel
import com.example.coffee.domain.CatefgoryModel
import com.example.coffee.domain.ItemModel
import com.example.coffee.repository.MainRepository


class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    fun loadBanner(): LiveData<List<BannerModel>> {
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<List<CatefgoryModel>> {
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<List<ItemModel>> {
        return repository.loadPopular()
    }

    fun loadItems(categoryId: String): LiveData<List<ItemModel>> {
        return repository.loadItemCategory(categoryId)

    }
}