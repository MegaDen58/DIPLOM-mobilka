package com.example.denistoptop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainAdapter
import com.example.denistoptop.data.MainData
import com.example.denistoptop.service.OrderService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SelectedOrder : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView
    private lateinit var cancelButton: Button
    private lateinit var orderApiService: OrderService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_order)

        recyclerView = findViewById(R.id.recyclerView)
        textView = findViewById(R.id.textView)
        cancelButton = findViewById(R.id.cancelButton)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        orderApiService = retrofit.create(OrderService::class.java)

        if (!GlobalVariables.order?.type.equals("Оплачен")) {
            cancelButton.isEnabled = false
            cancelButton.setBackgroundColor(resources.getColor(R.color.grey))
        }

        val user = com.example.denistoptop.dto.UserManager.getUserInfo(this)
        val order = GlobalVariables.order


        if(GlobalVariables.adminButton){
            if(user?.roles?.contains("ADMIN") == true && order?.type.equals("Оплачен")){
                cancelButton.setText("Сдать в аренду")
                cancelButton.setOnClickListener {
                    order?.let { it1 -> updateOrderType(it1.id, "Активен") }
                }
            }

            else if(user?.roles?.contains("ADMIN") == true && order?.type.equals("Активен")){
                cancelButton.text = "Завершить аренду"
                cancelButton.isEnabled = false
            }

            else if (user?.roles?.contains("ADMIN") == true && order?.type.equals("Активен") && isCurrentDateAfterOrEqual(
                    order?.end_date.toString()
                )) {
                cancelButton.text = "Завершить аренду"
                cancelButton.setOnClickListener {
                    order?.let { it1 -> updateOrderType(it1.id, "Завершен") }
                }
            }
        }
        else{
            cancelButton.setOnClickListener {

                val orderId = GlobalVariables.order?.id
                if (orderId != null) {
                    deleteOrder(orderId)
                }
            }
        }

        textView.text = "Стоимость корзины: ${GlobalVariables.allCartPrice}₽"

        val myDataset = GlobalVariables.selectedCart?.map {
            val imageUrl = if (it.images.isNotEmpty()) "http://94.228.112.46:8080/api/products/image/${it.images[0]}" else ""
            MainData(imageUrl, it.name, it.price.toString())
        } ?: emptyList()
        recyclerView.adapter = MainAdapter(myDataset)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun deleteOrder(orderId: Long) {
        orderApiService.deleteOrder(orderId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SelectedOrder, "Заказ отменён.", Toast.LENGTH_SHORT).show()

                    finish()
                    startActivity(Intent(this@SelectedOrder, HistoryActivity::class.java))
                } else {
                    Toast.makeText(this@SelectedOrder, "Ошибка при отмене заказа.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SelectedOrder, "Ошибка при отмене заказа.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateOrderType(orderId: Long, newType: String) {
        val jsonRequest = """
            {
                "orderId": $orderId,
                "type": "$newType"
            }
        """.trimIndent()

        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonRequest)
        orderApiService.updateOrderType(requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SelectedOrder, "Тип заказа обновлен", Toast.LENGTH_SHORT).show()
                    // Отключение кнопки и изменение ее цвета
                    cancelButton.isEnabled = false
                    cancelButton.setBackgroundColor(Color.GRAY)
                } else {
                    Toast.makeText(this@SelectedOrder, "Ошибка при обновлении типа заказа", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateOrderType", "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SelectedOrder, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isCurrentDateAfterOrEqual(endDateString: String?): Boolean {
        if (endDateString == null) return false

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust the format according to your end_date format
        return try {
            val endDate = dateFormat.parse(endDateString)
            val currentDate = Date()
            currentDate.after(endDate) || currentDate.equals(endDate)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}