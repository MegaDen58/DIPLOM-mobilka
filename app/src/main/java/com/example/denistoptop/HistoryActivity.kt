package com.example.denistoptop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.OnOrderClickListener
import com.example.denistoptop.adapter.OrderAdapter
import com.example.denistoptop.dto.OrderDto
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.dto.UserManager
import com.example.denistoptop.service.OrderService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistoryActivity: AppCompatActivity(){

    private lateinit var favouritesButton: ImageButton
    private lateinit var burgerButton: ImageButton
    private lateinit var cartButton: ImageButton
    private lateinit var mainButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private var allProducts: List<ProductDto>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_layout)

        toolbar = findViewById<Toolbar>(R.id.toolbar_layout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        favouritesButton = findViewById(R.id.favourites)
        burgerButton = findViewById(R.id.burgerMenu)
        cartButton = findViewById(R.id.cart)
        historyButton = findViewById(R.id.history)
        mainButton = findViewById(R.id.main)

        val toolbarClickListener = ToolbarButtonClickListener(this, toolbar, this)
        favouritesButton.setOnClickListener(toolbarClickListener)
        burgerButton.setOnClickListener(toolbarClickListener)
        cartButton.setOnClickListener(toolbarClickListener)
        mainButton.setOnClickListener(toolbarClickListener)

        historyButton.setBackgroundResource(R.drawable.selectedhistory)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OrderService::class.java)

        // Выполнение запроса к серверу
        service.getAllOrdersByUserId(UserManager.getUserInfo(this)!!.id).enqueue(object : Callback<List<OrderDto>> {
            override fun onResponse(call: Call<List<OrderDto>>, response: Response<List<OrderDto>>) {
                if (response.isSuccessful) {
                    val orders = response.body()
                    // Создание и установка адаптера RecyclerView
                    orders?.let {
                        // В методе onCreate вашей Activity
                        val adapter = OrderAdapter(orders, object : OnOrderClickListener {
                            override fun onOrderClick(order: OrderDto) {

                                val retrofit = Retrofit.Builder()
                                    .baseUrl("http://94.228.112.46:8080/api/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()

                                val apiService = retrofit.create(OrderService::class.java)

                                apiService.getProductsForOrder(order.id).enqueue(object : Callback<List<ProductDto>> {
                                    override fun onResponse(call: Call<List<ProductDto>>, response: Response<List<ProductDto>>) {
                                        if (response.isSuccessful) {
                                            val products = response.body()
                                            // Обработка полученных продуктов
                                            products?.let { productList ->
                                                GlobalVariables.selectedCart = products as MutableList<ProductDto>

                                                val intent = Intent(this@HistoryActivity, SelectedOrder::class.java)
                                                startActivity(intent)
                                                overridePendingTransition(0, 0)
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<List<ProductDto>>, t: Throwable) {
                                        // Обработка ошибки при выполнении запроса
                                    }
                                })

                            }
                        })
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<OrderDto>>, t: Throwable) {
                // Обработка ошибки при выполнении запроса
            }
        })


        recyclerView = findViewById<RecyclerView>(R.id.ordersRecyclerView)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager


        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}