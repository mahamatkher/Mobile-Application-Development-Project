package com.example.coffee.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee.Helper.ManagmentCart
import com.example.coffee.Helper.ManagmentWishlist
import com.example.coffee.R
import com.example.coffee.activites.DetailActivity
import com.example.coffee.databinding.ViewholderpopularBinding
import com.example.coffee.domain.ItemModel

class ItemListCategoryAdapter(
    private val context: Context,
    private val items: List<ItemModel>
) : RecyclerView.Adapter<ItemListCategoryAdapter.ViewHolder>() {

    private val managmentCart = ManagmentCart(context)
    private val managmentWishlist = ManagmentWishlist(context)

    class ViewHolder(val binding: ViewholderpopularBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ViewholderpopularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Adapt width to fill grid cell cleanly in 2-column grid
        holder.itemView.layoutParams?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            holder.itemView.layoutParams = it
        }

        holder.binding.titleText.text = item.title
        holder.binding.priceTxt.text = String.format(java.util.Locale.US, "$%.2f", item.price)
        holder.binding.subtitleTxt.text = if (item.extra.isNotEmpty()) item.extra else "Premium Roast"
        holder.binding.ratingTxt.text = if (item.rating > 0.0) item.rating.toString() else "4.8"

        if (item.picUrl.isNotEmpty()) {
            Glide.with(context)
                .load(item.picUrl[0])
                .into(holder.binding.pic)
        }

        // Set initial Wishlist status
        val isWishlisted = managmentWishlist.isWishlisted(item)
        if (isWishlisted) {
            holder.binding.favBtn.setImageResource(R.drawable.ic_heart_filled)
            holder.binding.favBtn.setColorFilter(ContextCompat.getColor(context, R.color.orange))
        } else {
            holder.binding.favBtn.setImageResource(R.drawable.ic_heart_outline)
            holder.binding.favBtn.setColorFilter(ContextCompat.getColor(context, R.color.lightBrown))
        }

        // Quick 1-tap Add to Cart
        holder.binding.imageView6.setOnClickListener {
            val itemToInsert = item.copy(numberInCart = if (item.numberInCart > 0) item.numberInCart else 1)
            managmentCart.insertItems(itemToInsert)
        }

        // Floating Favorite Toggle
        holder.binding.favBtn.setOnClickListener {
            val nowFav = managmentWishlist.toggleWishlist(item)
            if (nowFav) {
                holder.binding.favBtn.setImageResource(R.drawable.ic_heart_filled)
                holder.binding.favBtn.setColorFilter(ContextCompat.getColor(context, R.color.orange))
            } else {
                holder.binding.favBtn.setImageResource(R.drawable.ic_heart_outline)
                holder.binding.favBtn.setColorFilter(ContextCompat.getColor(context, R.color.lightBrown))
            }
        }

        // Open details
        holder.binding.cardRoot.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", item)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}
