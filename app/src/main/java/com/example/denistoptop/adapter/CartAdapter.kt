package com.example.denistoptop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.denistoptop.GlobalVariables
import com.example.denistoptop.R

class Item(val name: String, val imageUrl: String)


class CartAdapter(private val itemList: MutableList<Item>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemText: TextView = itemView.findViewById(R.id.itemText)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemText.text = item.name

        // Загрузка изображения из интернета с помощью Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.itemImage)

        // Обработка нажатия на значок удаления
        holder.deleteIcon.setOnClickListener {
            // Удаление элемента из списка и обновление RecyclerView
            itemList.removeAt(holder.adapterPosition)
            GlobalVariables.allCartPrice -= GlobalVariables.cart.get(holder.adapterPosition).price
            GlobalVariables.cart.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            // Проверка, если список пуст, обновить RecyclerView
            if (itemList.isEmpty()) {
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}