package com.example.coffee.activites

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coffee.Helper.ManagmentWishlist
import com.example.coffee.adapters.WishlistAdapter
import com.example.coffee.databinding.ActivityWishlistBinding
import com.example.coffee.domain.ItemModel

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding
    private lateinit var managmentWishlist: ManagmentWishlist
    private var wishlistItems = ArrayList<ItemModel>()
    private var adapter: WishlistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentWishlist = ManagmentWishlist(this)

        binding.backBtn.setOnClickListener { finish() }
        binding.exploreDrinksBtn.setOnClickListener { finish() }

        initWishlist()
    }

    override fun onResume() {
        super.onResume()
        initWishlist()
    }

    private fun initWishlist() {
        wishlistItems = managmentWishlist.getWishlist()
        updateBadgeAndEmptyState()

        binding.wishlistView.layoutManager = GridLayoutManager(this, 2)
        adapter = WishlistAdapter(this, wishlistItems) {
            updateBadgeAndEmptyState()
        }
        binding.wishlistView.adapter = adapter
    }

    private fun updateBadgeAndEmptyState() {
        val count = wishlistItems.size
        binding.itemCountBadge.text = "$count ${if (count == 1) "Drink" else "Drinks"}"

        if (count == 0) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.wishlistView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.wishlistView.visibility = View.VISIBLE
        }
    }
}
