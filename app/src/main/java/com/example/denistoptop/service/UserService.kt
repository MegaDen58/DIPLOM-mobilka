package com.example.denistoptop.service
import com.example.denistoptop.dto.UserDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    fun loginUser(@Body userJson: RequestBody): Call<UserDto>
    @POST("users/register")
    fun registerUser(@Body userJson: RequestBody): Call<UserDto>
}