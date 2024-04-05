package com.example.denistoptop.service
import com.example.denistoptop.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    fun loginUser(@Body userJson: String): Call<User>
}