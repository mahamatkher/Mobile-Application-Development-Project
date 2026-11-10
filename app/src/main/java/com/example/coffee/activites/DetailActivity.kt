package com.example.coffee.activites

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.example.coffee.Helper.ManagmentCart
import com.example.coffee.Helper.ManagmentWishlist
import com.example.coffee.R
import com.example.coffee.databinding.ActivityDetailBinding
import com.example.coffee.domain.ItemModel
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel
    private lateinit var managmentCart: ManagmentCart
    private lateinit var managmentWishlist: ManagmentWishlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)
        managmentWishlist = ManagmentWishlist(this)

        setupWindowInsets()
        bundle()
        initSizeList()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.headerLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBar.top + 8
            }

            binding.bottomBar.setPadding(
                binding.bottomBar.paddingLeft,
                binding.bottomBar.paddingTop,
                binding.bottomBar.paddingRight,
                navBar.bottom + 16
            )
            insets
        }
    }

    private fun selectSize(selectedBtn: TextView, unselected1: TextView, unselected2: TextView, sizeName: String) {
        selectedBtn.setBackgroundResource(R.drawable.drowel_full_corner_db)
        selectedBtn.setTextColor(ContextCompat.getColor(this, R.color.white))

        unselected1.setBackgroundResource(0)
        unselected1.setTextColor(ContextCompat.getColor(this, R.color.lightBrown))

        unselected2.setBackgroundResource(0)
        unselected2.setTextColor(ContextCompat.getColor(this, R.color.lightBrown))

        item.size = sizeName
    }

    private fun initSizeList() {
        binding.apply {
            selectSize(SmallBtn, MediumBtn, LargeBtn, "Small")

            SmallBtn.setOnClickListener {
                selectSize(SmallBtn, MediumBtn, LargeBtn, "Small")
            }

            MediumBtn.setOnClickListener {
                selectSize(MediumBtn, SmallBtn, LargeBtn, "Medium")
            }

            LargeBtn.setOnClickListener {
                selectSize(LargeBtn, SmallBtn, MediumBtn, "Large")
            }
        }
    }

    private fun updatePrice() {
        val count = item.numberInCart.coerceAtLeast(1)
        val totalPrice = item.price * count
        binding.priceTxt.text = String.format(Locale.US, "$%.2f", totalPrice)
    }

    private fun bundle() {
        val serializableItem = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("object", ItemModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("object") as? ItemModel
        }

        if (serializableItem == null) {
            finish()
            return
        }
        item = serializableItem

        binding.apply {
            if (item.picUrl.isNotEmpty()) {
                Glide.with(this@DetailActivity)
                    .load(item.picUrl[0])
                    .fitCenter()
                    .into(picMain)
            }

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            ratingTxt.text = item.rating.toString()

            if (item.numberInCart <= 0) {
                item.numberInCart = 1
            }
            numberCartTxt.text = item.numberInCart.toString()
            updatePrice()

            // Wishlist integration
            var isFav = managmentWishlist.isWishlisted(item)
            updateFavIcon(isFav)

            favBtn.setOnClickListener {
                isFav = managmentWishlist.toggleWishlist(item)
                updateFavIcon(isFav)
            }

            addToCartBtn.setOnClickListener {
                val quantity = numberCartTxt.text.toString().toIntOrNull() ?: 1
                item.numberInCart = if (quantity > 0) quantity else 1
                managmentCart.insertItems(item)
            }

            backBtn.setOnClickListener { finish() }

            plusBtn.setOnClickListener {
                item.numberInCart++
                numberCartTxt.text = item.numberInCart.toString()
                updatePrice()
            }

            minusBtn.setOnClickListener {
                if (item.numberInCart > 1) {
                    item.numberInCart--
                    numberCartTxt.text = item.numberInCart.toString()
                    updatePrice()
                }
            }
        }
    }

    private fun updateFavIcon(isFav: Boolean) {
        if (isFav) {
            binding.favBtn.setImageResource(R.drawable.ic_heart_filled)
            binding.favBtn.setColorFilter(ContextCompat.getColor(this, R.color.orange))
        } else {
            binding.favBtn.setImageResource(R.drawable.ic_heart_outline)
            binding.favBtn.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }
    }
}
