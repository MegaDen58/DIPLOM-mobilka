package com.example.denistoptop.service

import com.example.denistoptop.dto.OrderDto
import com.example.denistoptop.dto.ProductDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {
    @POST("orders/create")
    fun createOrder(@Body orderDto: RequestBody): Call<OrderDto>
    @POST("orders/delete/{orderId}")
    fun deleteOrder(@Path("orderId") orderId: Long): Call<Void>
    @GET("orders/user/{userId}")
    fun getAllOrdersByUserId(@Path("userId") userId: Long): Call<List<OrderDto>>
    @GET("orders/{orderId}/products")
    fun getProductsForOrder(@Path("orderId") orderId: Long): Call<List<ProductDto>>
    @GET("orders/all")
    fun getAllProducts(): Call<List<ProductDto>>
}