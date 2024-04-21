package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainItemImageAdapter
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.dto.UserDto
import com.example.denistoptop.service.UserService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainItemActivity : AppCompatActivity() {

    private lateinit var textProductName: TextView
    private lateinit var textCount: TextView
    private lateinit var textDescription: TextView
    private lateinit var textSuitableFor: TextView
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var toolbar: Toolbar
    var isFavourite = false

    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_item)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)

        textProductName = findViewById(R.id.textProductName)
        textCount = findViewById(R.id.textCount)
        textSuitableFor = findViewById(R.id.textSuitableFor)
        recyclerViewImages = findViewById(R.id.recyclerViewImages)
        textDescription = findViewById(R.id.textDescription)
        toolbar = findViewById(R.id.toolbar)

        val product: ProductDto? = intent.getSerializableExtra("product") as? ProductDto
        val myButton = findViewById<ImageButton>(R.id.myButton)
        myButton.setOnClickListener {
            onBackPressed()
        }

        val secondButton = findViewById<ImageButton>(R.id.secondButton)

        val call = userService.getUserById(GlobalVariables.userId.toString())
        Log.d("USERID", GlobalVariables.userId.toString())
        call.enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        if(user.favourites.contains(product?.id?.toInt())){
                            secondButton.setImageResource(R.drawable.heart)
                            isFavourite = true
                        }
                        else{
                            secondButton.setImageResource(R.drawable.noheart)
                            isFavourite = false
                        }
                    } else {
                        Toast.makeText(this@MainItemActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainItemActivity, "Ошибка при получении пользователя", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
                Toast.makeText(this@MainItemActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        secondButton.setOnClickListener {

            val call = userService.getUserById(GlobalVariables.userId.toString())
            call.enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {

                            if(!isFavourite){
                                val jsonFavourites = "{\"userId\": \"${user.id}\", \"productId\": \"${product?.id}\"}"
                                val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonFavourites)
                                val callIn = userService.addFavourite(requestBody)

                                callIn.enqueue(object : Callback<UserDto> {
                                    override fun onResponse(
                                        call: Call<UserDto>,
                                        response: Response<UserDto>
                                    ) {
                                        if (response.isSuccessful) {
                                            val user = response.body()
                                            secondButton.setImageResource(R.drawable.heart)
                                            isFavourite = true
                                        } else {
                                            // Обработка ошибки
                                        }
                                    }
                                    override fun onFailure(call: Call<UserDto>, t: Throwable) {
                                        // Обработка ошибок сети или других ошибок
                                    }
                                })
                            }
                            else{
                                val jsonFavourites = "{\"userId\": \"${user.id}\", \"productId\": \"${product?.id}\"}"
                                val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonFavourites)
                                val callIn = userService.removeFavourite(requestBody)

                                callIn.enqueue(object : Callback<UserDto> {
                                    override fun onResponse(
                                        call: Call<UserDto>,
                                        response: Response<UserDto>
                                    ) {
                                        if (response.isSuccessful) {
                                            val user = response.body()
                                            secondButton.setImageResource(R.drawable.noheart)
                                            isFavourite = false
                                        } else {
                                            // Обработка ошибки
                                        }
                                    }
                                    override fun onFailure(call: Call<UserDto>, t: Throwable) {
                                        // Обработка ошибок сети или других ошибок
                                    }
                                })
                            }

                        } else {
                            Toast.makeText(this@MainItemActivity, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainItemActivity, "Ошибка при получении пользователя", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    // Обработка ошибок сети или других ошибок
                    Toast.makeText(this@MainItemActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
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