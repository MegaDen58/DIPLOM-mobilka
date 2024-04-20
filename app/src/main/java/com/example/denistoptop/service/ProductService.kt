package com.example.denistoptop.service

import com.example.denistoptop.dto.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductService {
    @POST("products/create")
    //suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>
    fun createProduct(@Body productDto: RequestBody): Call<ProductDto>
    @Multipart
    @POST("products/upload/image") // Замените на конечную точку вашего сервера для загрузки изображений
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody> // Замените на ваш класс ответа от сервера

    @GET("/api/products/all")
    fun getAllProducts(): Call<List<ProductDto>>

    @GET("products/product/{imageName}")
    fun getProductByImage(@Path("imageName") imageName: String): Call<ProductDto>
}