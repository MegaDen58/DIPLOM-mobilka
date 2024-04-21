package com.example.denistoptop.service
import com.example.denistoptop.dto.UserDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("users/login")
    fun loginUser(@Body userJson: RequestBody): Call<UserDto>
    @POST("users/register")
    fun registerUser(@Body userJson: RequestBody): Call<UserDto>
    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: String): Call<UserDto>
    @POST("users/addFavourite")
    fun addFavourite(@Body request: RequestBody): Call<UserDto>
    @POST("users/removeFavourite")
    fun removeFavourite(@Body request: RequestBody): Call<UserDto>

}