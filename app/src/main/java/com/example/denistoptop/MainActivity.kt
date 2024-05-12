package com.example.denistoptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
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

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService
    private lateinit var favouritesButton: ImageButton
    private lateinit var burgerButton: ImageButton
    private lateinit var cartButton: ImageButton
    private lateinit var mainButton: ImageButton
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private var products: List<ProductDto>? = null
    private var filteredProducts: List<ProductDto>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchEditText = findViewById(R.id.search)
        toolbar = findViewById<Toolbar>(R.id.toolbar_layout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        favouritesButton = findViewById(R.id.favourites)
        burgerButton = findViewById(R.id.burgerMenu)
        cartButton = findViewById(R.id.cart)
        mainButton = findViewById(R.id.main)
        val toolbarClickListener = ToolbarButtonClickListener(this, toolbar, this)
        favouritesButton.setOnClickListener(toolbarClickListener)
        burgerButton.setOnClickListener(toolbarClickListener)
        cartButton.setOnClickListener(toolbarClickListener)
        mainButton.setOnClickListener(toolbarClickListener)

        mainButton.setBackgroundResource(R.drawable.selectedmain)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
        })

        // Получение списка продуктов
        productService.getAllProducts().enqueue(object : Callback<List<ProductDto>> {
            override fun onResponse(call: Call<List<ProductDto>>, response: Response<List<ProductDto>>) {
                if (response.isSuccessful) {
                    products = response.body()
                    filteredProducts = products
                    showFilteredProducts()
                } else {
                    // Обработка неудачного запроса
                }
            }
            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
            }
        })
    }

    private fun filterProducts(query: String) {
        filteredProducts = if (query.isBlank()) {
            products
        } else {
            products?.filter {
                it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            }
        }
        showFilteredProducts()
    }

    private fun showFilteredProducts() {
        val myDataset = filteredProducts?.map {
            val imageUrl = if (it.images.isNotEmpty()) "http://94.228.112.46:8080/api/products/image/${it.images[0]}" else "" // URL первого изображения
            MainData(imageUrl, it.name)
        } ?: emptyList()
        recyclerView.adapter = MainAdapter(myDataset)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}