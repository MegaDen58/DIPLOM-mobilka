package com.example.denistoptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import com.example.denistoptop.service.ProductService
import com.example.denistoptop.service.UserService
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainItemActivity : AppCompatActivity() {

    private lateinit var textProductName: TextView
    private lateinit var textDescription: TextView
    private lateinit var textSuitableFor: TextView
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var cartButton: Button
    private lateinit var deleteButton: ImageButton
    private lateinit var editButton: ImageButton

    var isFavourite = false

    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService
    private lateinit var productService: ProductService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_item)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
        productService = retrofit.create(ProductService::class.java)

        textProductName = findViewById(R.id.textProductName)
        textSuitableFor = findViewById(R.id.textSuitableFor)
        recyclerViewImages = findViewById(R.id.recyclerViewImages)
        textDescription = findViewById(R.id.textDescription)
        cartButton = findViewById(R.id.cartButton)
        editButton = findViewById(R.id.editButton)

        toolbar = findViewById(R.id.toolbar)

        val product: ProductDto? = intent.getSerializableExtra("product") as? ProductDto
        val myButton = findViewById<ImageButton>(R.id.myButton)
        myButton.setOnClickListener {
            onBackPressed()
        }

        val secondButton = findViewById<ImageButton>(R.id.secondButton)
        deleteButton = findViewById<ImageButton>(R.id.deleteButton)


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

        deleteButton.setOnClickListener {
            product?.let {
                val productId = it.id
                if (productId != null) {
                    productService.deleteProduct(productId).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MainItemActivity, "Продукт удален", Toast.LENGTH_SHORT).show()
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(this@MainItemActivity, "Ошибка при удалении продукта", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@MainItemActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this@MainItemActivity, EditProductActivity::class.java)
            intent.putExtra("editProduct", product)
            startActivity(intent)
        }


        cartButton.setOnClickListener {
            product?.let { it1 -> GlobalVariables.cart.add(it1) }
        }

        // Установка названия продукта
        textProductName.text = product?.name ?: "ERROR"
        textDescription.setText("Описание: " + product?.description)

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