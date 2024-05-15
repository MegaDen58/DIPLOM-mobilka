package com.example.denistoptop

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.GlobalVariables.Companion.cart
import com.example.denistoptop.adapter.CartAdapter
import com.example.denistoptop.adapter.Item
import com.example.denistoptop.dto.OrderDto
import com.example.denistoptop.dto.UserDto
import com.example.denistoptop.dto.UserManager
import com.example.denistoptop.service.OrderService
import com.example.denistoptop.service.ProductService
import com.example.denistoptop.service.UserService
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CartActivity : AppCompatActivity() {

    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var rentButton: Button
    private lateinit var favouritesButton: ImageButton
    private lateinit var burgerButton: ImageButton
    private lateinit var cartButton: ImageButton
    private lateinit var mainButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var toolbar: Toolbar

    private lateinit var retrofit: Retrofit
    private lateinit var productService: ProductService
    private lateinit var userService: UserService
    private lateinit var orderService: OrderService


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val startDate: LocalDate = LocalDate.now()
        val endDate: LocalDate = LocalDate.now()
        toolbar = findViewById<Toolbar>(R.id.toolbar_layout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mainButton = findViewById(R.id.main)
        startDatePicker = findViewById(R.id.startDatePicker)
        endDatePicker = findViewById(R.id.endDatePicker)
        rentButton = findViewById(R.id.rentButton)
        favouritesButton = findViewById(R.id.favourites)
        burgerButton = findViewById(R.id.burgerMenu)
        cartButton = findViewById(R.id.cart)
        historyButton = findViewById(R.id.history)
        val toolbarClickListener = ToolbarButtonClickListener(this, toolbar, this)
        favouritesButton.setOnClickListener(toolbarClickListener)
        favouritesButton.setBackgroundResource(R.drawable.noselectedheart)
        cartButton.setBackgroundResource(R.drawable.selectedcart)
        burgerButton.setOnClickListener(toolbarClickListener)
        mainButton.setOnClickListener(toolbarClickListener)
        historyButton.setOnClickListener(toolbarClickListener)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val rentButton: Button = findViewById(R.id.rentButton)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productService = retrofit.create(ProductService::class.java)
        orderService = retrofit.create(OrderService::class.java)
        userService = retrofit.create(UserService::class.java)

        // Создание списка предметов
        val itemList = mutableListOf<Item>()

        for (product in GlobalVariables.cart) {
            GlobalVariables.allCartPrice += product.price
            val imageUrl = if (product.images.isNotEmpty()) {
                "http://94.228.112.46:8080/api/products/image/${product.images[0]}"
            } else {
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
            val differenceInDays = calculateDifferenceInDays() + 1
            val rentPrice = differenceInDays * GlobalVariables.allCartPrice
            Log.w("DIF", differenceInDays.toString())
//            val message = "Арендовать ($rentPrice рублей)"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = UserManager.getUserInfo(this)
            if (user!!.balance >= rentPrice) {
                var newBalance = user!!.balance - rentPrice
                var userId = user.id

                val jsonString = "{\"userId\": $userId, \"balance\": $newBalance}"

                val requestBody =
                    RequestBody.create(MediaType.parse("application/json"), jsonString)

                val call = userService.setUserBalance(requestBody)
                call.enqueue(object : Callback<UserDto> {
                    override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                        if (response.isSuccessful) {

                            val startYear = startDatePicker.year
                            val startMonth = startDatePicker.month + 1
                            val startDayOfMonth = startDatePicker.dayOfMonth
                            val selectedStartDate = "${startYear}-${
                                String.format(
                                    "%02d",
                                    startMonth
                                )
                            }-${String.format("%02d", startDayOfMonth)}"


                            val endYear = endDatePicker.year
                            val endMonth =
                                endDatePicker.month + 1 // Увеличиваем на 1, так как месяцы начинаются с 0
                            val endDayOfMonth = endDatePicker.dayOfMonth
                            val selectedEndDate = "${endYear}-${
                                String.format(
                                    "%02d",
                                    endMonth
                                )
                            }-${String.format("%02d", endDayOfMonth)}"

                            val productList = arrayListOf<Long>()

                            for (product in cart) {
                                productList.add(product.id)
                            }

                            val orderJsonString =
                                "{\"start_date\": \"$selectedStartDate\", \"end_date\": \"$selectedEndDate\", " +
                                        "\"price\": ${rentPrice}, \"user_id\": ${user.id}, " +
                                        "\"type\": \"Оплачен\", \"items\": ${JSONArray(productList)}}"

                            val requestOrderBody = RequestBody.create(
                                MediaType.parse("application/json"),
                                orderJsonString
                            )
                            orderService.createOrder(requestOrderBody)
                                .enqueue(object : Callback<OrderDto> {
                                    override fun onResponse(
                                        call: Call<OrderDto>,
                                        response: Response<OrderDto>
                                    ) {
                                        Log.w("TAGI", orderJsonString)
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                this@CartActivity,
                                                "Арендовано",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@CartActivity,
                                                "пздц1",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }

                                    override fun onFailure(call: Call<OrderDto>, t: Throwable) {
                                        Toast.makeText(
                                            this@CartActivity,
                                            "пздц2",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                })
                        } else {
                            Toast.makeText(
                                this@CartActivity,
                                "Ошибка при аренде",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UserDto>, t: Throwable) {
                        Log.e("ERROR", "Ошибка при выполнении запроса: ${t.message}")
                        Toast.makeText(
                            this@CartActivity,
                            "Ошибка при выполнении запроса",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })


            } else {
                Toast.makeText(
                    this@CartActivity,
                    "На балансе недостаточно средств",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
        val differenceInDays = calculateDifferenceInDays() + 1
        val rentPrice = differenceInDays * GlobalVariables.allCartPrice
        val buttonText = "Арендовать ($rentPrice рублей)"
        rentButton.text = buttonText
    }

}