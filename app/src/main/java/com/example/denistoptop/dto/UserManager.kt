package com.example.denistoptop.dto

import android.content.Context
import com.google.gson.Gson

class UserManager {
    companion object {
        fun saveUserInfo(context: Context, user: UserDto?) {
            val sharedPreferences = context.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val userJson = gson.toJson(user)
            editor.putString("USER_INFO", userJson)
            editor.apply()
        }

        fun getUserInfo(context: Context): UserDto? {
            val sharedPreferences = context.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
            val gson = Gson()
            val userJson = sharedPreferences.getString("USER_INFO", null)
            return gson.fromJson(userJson, UserDto::class.java)
        }
    }
}