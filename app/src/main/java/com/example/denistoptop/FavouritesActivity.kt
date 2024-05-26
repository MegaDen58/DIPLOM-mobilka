package com.example.denistoptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainAdapter
import com.example.denistoptop.data.MainData
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavouritesActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService
    private lateinit var favouritesButton: ImageButton
    private lateinit var burgerButton: ImageButton
    private lateinit var mainButton: ImageButton
    private lateinit var cartButton: ImageButton
    private lateinit var historyButton: ImageButton

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        toolbar = findViewById<Toolbar>(R.id.toolbar_layout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        favouritesButton = findViewById(R.id.favourites)
        burgerButton = findViewById(R.id.burgerMenu)
        mainButton = findViewById(R.id.main)
        cartButton = findViewById(R.id.cart)
        historyButton = findViewById(R.id.history)

        val toolbarClickListener = ToolbarButtonClickListener(this, toolbar, this)
        favouritesButton.setOnClickListener(toolbarClickListener)
        favouritesButton.setBackgroundResource(R.drawable.selectedheart)
        burgerButton.setOnClickListener(toolbarClickListener)
        mainButton.setOnClickListener(toolbarClickListener)
        cartButton.setOnClickListener(toolbarClickListener)
        historyButton.setOnClickListener(toolbarClickListener)

        productService = retrofit.create(ProductService::class.java)

        // Получение списка продуктов
        productService.getAllProductsByUserId(GlobalVariables.userId).enqueue(object : Callback<List<ProductDto>> {
            override fun onResponse(call: Call<List<ProductDto>>, response: Response<List<ProductDto>>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    val myDataset = products?.map {
                        val imageUrl = if (it.images.isNotEmpty()) "http://94.228.112.46:8080/api/products/image/${it.images[0]}" else "" // URL первого изображения
                        MainData(imageUrl, it.name, it.price.toString())
                    } ?: emptyList()

                    recyclerView.adapter = MainAdapter(myDataset)
                    recyclerView.layoutManager = LinearLayoutManager(this@FavouritesActivity)
                    Log.w("GOOD", "GOOD")
                } else {
                    Log.w("BAD1", "BAD1")
                }
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                Log.w("BAD2", "BAD2")
            }
        })

    }
}