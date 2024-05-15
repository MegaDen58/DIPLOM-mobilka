package com.example.denistoptop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.R
import com.example.denistoptop.dto.OrderDto

class OrderAdapter(private val orders: List<OrderDto>, private val listener: OnOrderClickListener) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // ViewHolder для элемента заказа
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val orderPriceTextView: TextView = itemView.findViewById(R.id.orderPriceTextView)
        val orderStatusTextView: TextView = itemView.findViewById(R.id.orderStatusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_item_layout, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]

        // Устанавливаем данные в TextView
        holder.orderIdTextView.text = "ID: ${currentOrder.id} | "
        holder.orderPriceTextView.text = "Цена: ${currentOrder.price} | "
        holder.orderStatusTextView.text = "Статус: ${currentOrder.type}"

        // Обработка нажатия на элемент
        holder.itemView.setOnClickListener {
            listener.onOrderClick(currentOrder)
        }
    }

    override fun getItemCount() = orders.size
}
interface OnOrderClickListener {
    fun onOrderClick(order: OrderDto)
}