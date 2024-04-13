package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainAdapter
import com.example.denistoptop.data.MainData
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.ProductService
import com.example.denistoptop.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)

        // Получение списка продуктов
        productService.getAllProducts().enqueue(object : Callback<List<ProductDto>> {
            override fun onResponse(call: Call<List<ProductDto>>, response: Response<List<ProductDto>>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    val myDataset = products?.map {
                        val imageUrl = if (it.images.isNotEmpty()) "http://94.228.112.46:8080/api/products/image/${it.images[0]}" else "" // URL первого изображения
                        MainData(imageUrl, it.name)
                    } ?: emptyList()

                    recyclerView.adapter = MainAdapter(myDataset)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                } else {
                    // Обработка неудачного запроса
                }
            }

            override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
            }
        })
    }
}