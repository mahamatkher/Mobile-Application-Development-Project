package com.example.coffee.adapters

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.example.coffee.R
import com.example.coffee.activites.ItemsListActivity
import com.example.coffee.databinding.ViewholderCategoryBinding
import com.example.coffee.domain.CatefgoryModel
import android.os.Handler

class CategoryAdapter(
    private val items: List<CatefgoryModel>,
    private val context: Context
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    class ViewHolder(val binding: ViewholderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.titleCat.text = item.title // Set the title of the category

        holder.binding.root.setOnClickListener {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(context, ItemsListActivity::class.java).apply {
                    putExtra("id", item.id.toString())
                    putExtra("title", item.title)
                }
                ContextCompat.startActivity(context, intent, null)
            }, 500L)

        }


        if (selectedPosition == position) {
            holder.binding.titleCat.setBackgroundResource(
                R.drawable.drowel_full_corner_db
            )
        }
        else {
            holder.binding.titleCat.setBackgroundResource(
                R.drawable.brown_2_full_corner
            )
        }
    }

    override fun getItemCount(): Int = items.size
}