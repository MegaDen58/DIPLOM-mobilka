package com.example.denistoptop.service

import com.example.denistoptop.dto.ProductDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductService {
    @POST("products/create")
    //suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>
    fun createProduct(@Body productDto: RequestBody): Call<ProductDto>
}