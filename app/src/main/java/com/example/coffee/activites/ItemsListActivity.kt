package com.example.coffee.activites

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coffee.R
import com.example.coffee.adapters.ItemListCategoryAdapter
import com.example.coffee.adapters.PopularAdapter
import com.example.coffee.databinding.ActivityItemsListBinding
import com.example.coffee.viewmodel.MainViewModel

class ItemsListActivity : AppCompatActivity() {
    lateinit var binding: ActivityItemsListBinding // Fix: Changed 'latinity' to 'lateinit'
    private val viewModel = MainViewModel()// Fix: Changed ': MainViewModel()' to '= MainViewModel()'
    private lateinit var id: String               // Fix: Changed 'privet val' to 'private lateinit var'
    private lateinit var title: String            // Fix: Changed 'privet val' to 'private lateinit var'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundles()
        initList()
    }

    private fun initList() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemsListActivity
                , Observer {
                    listView.layoutManager= GridLayoutManager(this@ItemsListActivity,2)
                    listView.adapter= ItemListCategoryAdapter(this@ItemsListActivity, it) // Line 39
                    progressBar.visibility= View.GONE
                })
            backBtn.setOnClickListener { finish() }
        }
    }

    private fun getBundles() {
        id = intent.getStringExtra("id")?: ""
        title = intent.getStringExtra("title")?:""

        binding.categoryTxt.text = title
    }
}