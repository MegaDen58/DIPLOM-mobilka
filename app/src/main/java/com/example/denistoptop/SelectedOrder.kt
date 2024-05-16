package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainAdapter
import com.example.denistoptop.data.MainData
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.OrderService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectedOrder : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_order)

        recyclerView = findViewById(R.id.recyclerView)

        val myDataset = GlobalVariables.selectedCart?.map {
            val imageUrl = if (it.images.isNotEmpty()) "http://94.228.112.46:8080/api/products/image/${it.images[0]}" else "" // URL первого изображения
            MainData(imageUrl, it.name)
        } ?: emptyList()
        recyclerView.adapter = MainAdapter(myDataset)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}