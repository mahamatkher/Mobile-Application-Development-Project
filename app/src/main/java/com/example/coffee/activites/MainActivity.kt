package com.example.coffee.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffee.adapters.CategoryAdapter
import com.example.coffee.adapters.PopularAdapter
import com.example.coffee.databinding.ActivityMainBinding
import com.example.coffee.viewmodel.MainViewModel

class  MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initCategory()
        initPopular()
        initBottomNavigation()

        binding.imageView2.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.imageView3.setOnClickListener {
            Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show()
        }

        binding.imageView4.setOnClickListener {
            Toast.makeText(this, "Filters coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.textView5.setOnClickListener {
            val intent = Intent(this, ItemsListActivity::class.java).apply {
                putExtra("id", "0")
                putExtra("title", "All Coffees")
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val tinyDB = com.example.coffee.Helper.TinyDB(this)
        val savedName = tinyDB.getString("user_name")
        if (savedName.isNotEmpty()) {
            binding.textView2.text = savedName
        } else {
            binding.textView2.text = "Guest"
        }
    }

    private fun initBottomNavigation() {
        binding.exploreBtn.setOnClickListener {
            binding.scrollView2.smoothScrollTo(0, 0)
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.wishlistBtn.setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        binding.orderBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE

        binding.popularView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewModel.loadPopular().observe(this) { items ->
            binding.popularView.adapter = PopularAdapter(this, items.toMutableList())
            binding.progressBarPopular.visibility = View.GONE
        }
    }
    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        binding.categoryView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        viewModel.loadCategory().observe(this) { categories ->
            binding.categoryView.adapter = CategoryAdapter(categories, this)

            binding.progressBarCategory.visibility = View.GONE
        }
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE

        viewModel.loadBanner().observe(this) { banners ->
            if (banners.isNotEmpty()) {
                Glide.with(this)
                    .load(banners[0].url)
                    .into(binding.banner)
            }
            binding.progressBarBanner.visibility = View.GONE
        }
    }
}