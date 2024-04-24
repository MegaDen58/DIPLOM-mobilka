package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.CartAdapter
import com.example.denistoptop.adapter.Item
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val bottomButton: Button = findViewById(R.id.bottomButton)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)

        // Создание списка предметов
        val itemList = mutableListOf<Item>()

        for(product in GlobalVariables.cart){
            val imageUrl = if (product.images.isNotEmpty()) {
                "http://94.228.112.46:8080/api/products/image/${product.images[0]}"
            } else {
                "" // Пустая строка, если изображение отсутствует
            }
            itemList.add(Item(product.name, imageUrl))
        }

        val adapter = CartAdapter(itemList)
        recyclerView.adapter = adapter


        bottomButton.setOnClickListener {
            // Обработка нажатия на кнопку внизу экрана
            Toast.makeText(this, "Нажата кнопка внизу", Toast.LENGTH_SHORT).show()
        }
    }
}