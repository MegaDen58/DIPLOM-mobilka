package com.example.denistoptop.service

import com.example.denistoptop.dto.OrderDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("orders/create")
    fun createOrder(@Body orderDto: RequestBody): Call<OrderDto>
}