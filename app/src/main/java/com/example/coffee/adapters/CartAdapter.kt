package com.example.coffee.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffee.Helper.ChangeNumberItemsListener
import com.example.coffee.Helper.ManagmentCart
import com.example.coffee.domain.ItemModel
import com.example.coffee.databinding.ViewholderCartBinding
import java.util.Locale

class CartAdapter(
    private val listItemSelected: ArrayList<ItemModel>,
    context: Context,
    val changeNumberItemsListener: ChangeNumberItemsListener? = null
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderCartBinding)
        : RecyclerView.ViewHolder(binding.root)

    private val managmentCart = ManagmentCart(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = listItemSelected[position]
        
        holder.binding.titleTxt.text = item.title
        val sizeStr = if (item.size.isNotBlank()) item.size else "Regular"
        holder.binding.sizeBadgeTxt.text = "☕ $sizeStr"
        holder.binding.feeEachItem.text = String.format(Locale.US, "• $%.2f each", item.price)
        holder.binding.totalEachItem.text = String.format(Locale.US, "$%.2f", item.numberInCart * item.price)
        holder.binding.numberCartTxt.text = item.numberInCart.toString()

        if (item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])
                .fitCenter()
                .into(holder.binding.picCatr)
        }

        holder.binding.plusBtn.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                managmentCart.plusItem(listItemSelected, pos, object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyItemChanged(pos)
                        changeNumberItemsListener?.onChanged()
                    }
                })
            }
        }

        holder.binding.minusBtn.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                managmentCart.minusItem(listItemSelected, pos, object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }
                })
            }
        }

        holder.binding.removeItemBtn.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                managmentCart.removeItem(listItemSelected, pos, object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}
