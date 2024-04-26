package com.example.denistoptop

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.CartAdapter
import com.example.denistoptop.adapter.Item
import com.example.denistoptop.service.ProductService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CartActivity : AppCompatActivity() {

    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var rentButton: Button
    private lateinit var favouritesButton: ImageButton

    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val startDate: LocalDate = LocalDate.now()
        val endDate: LocalDate = LocalDate.now()

        startDatePicker = findViewById(R.id.startDatePicker)
        endDatePicker = findViewById(R.id.endDatePicker)
        rentButton = findViewById(R.id.rentButton)
        favouritesButton = findViewById(R.id.favourites)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val rentButton: Button = findViewById(R.id.rentButton)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)

        // Создание списка предметов
        val itemList = mutableListOf<Item>()

        for(product in GlobalVariables.cart){
            GlobalVariables.allCartPrice += product.price
            val imageUrl = if (product.images.isNotEmpty()) {
                "http://94.228.112.46:8080/api/products/image/${product.images[0]}"
            }
            else {
                "" // Пустая строка, если изображение отсутствует
            }
            itemList.add(Item(product.name, imageUrl))
        }

        val adapter = CartAdapter(itemList)
        recyclerView.adapter = adapter

        startDatePicker.init(
            startDate.year, startDate.monthValue - 1, startDate.dayOfMonth
        ) { _, _, _, _ ->
            updateRentButtonText()
        }

        endDatePicker.init(
            endDate.year, endDate.monthValue - 1, endDate.dayOfMonth
        ) { _, _, _, _ ->
            updateRentButtonText()
        }

        updateRentButtonText()

        rentButton.setOnClickListener {
            // Обработка нажатия на кнопку аренды
            val differenceInDays = calculateDifferenceInDays()
            val rentPrice = differenceInDays * GlobalVariables.allCartPrice
            val message = "Арендовать ($rentPrice рублей)"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        favouritesButton.setOnClickListener{
            val intent = Intent(this@CartActivity, FavouritesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDifferenceInDays(): Long {
        val startDate = LocalDate.of(
            startDatePicker.year,
            startDatePicker.month + 1,
            startDatePicker.dayOfMonth
        )
        val endDate = LocalDate.of(
            endDatePicker.year,
            endDatePicker.month + 1,
            endDatePicker.dayOfMonth
        )
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRentButtonText() {
        val differenceInDays = calculateDifferenceInDays()
        val rentPrice = differenceInDays * GlobalVariables.allCartPrice
        val buttonText = "Арендовать ($rentPrice рублей)"
        rentButton.text = buttonText
    }
}