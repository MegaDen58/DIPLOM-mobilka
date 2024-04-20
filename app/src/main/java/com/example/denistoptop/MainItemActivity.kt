package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainItemImageAdapter
import com.example.denistoptop.dto.ProductDto

class MainItemActivity : AppCompatActivity() {

    private lateinit var textProductName: TextView
    private lateinit var textCount: TextView
    private lateinit var textDescription: TextView
    private lateinit var textSuitableFor: TextView
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_item)

        textProductName = findViewById(R.id.textProductName)
        textCount = findViewById(R.id.textCount)
        textSuitableFor = findViewById(R.id.textSuitableFor)
        recyclerViewImages = findViewById(R.id.recyclerViewImages)
        textDescription = findViewById(R.id.textDescription)
        toolbar = findViewById(R.id.toolbar)

        val myButton = findViewById<ImageButton>(R.id.myButton)
        myButton.setOnClickListener {
            onBackPressed()
        }

        val product: ProductDto? = intent.getSerializableExtra("product") as? ProductDto

        // Установка названия продукта
        textProductName.text = product?.name ?: "ERROR"
        textDescription.setText("Описание: " + product?.description)

        // Установка количества
        textCount.text = "Количество: ${product?.count ?: 0}"

        // Установка подходит для
        val suitableForText = when {
            product?.isSummer == true && product.isWinter == true -> "Подходит для всех сезонов"
            product?.isSummer == true -> "Подходит для лета"
            product?.isWinter == true -> "Подходит для зимы"
            else -> ""
        }
        textSuitableFor.text = suitableForText

        // Получение списка URL-адресов изображений
        val imageUrls = product?.images?.map { imageName -> "http://94.228.112.46:8080/api/products/image/$imageName" } ?: emptyList()

        // Настройка RecyclerView и адаптера
        recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewImages)
        val imageAdapter = MainItemImageAdapter(imageUrls)
        recyclerViewImages.adapter = imageAdapter

    }
}