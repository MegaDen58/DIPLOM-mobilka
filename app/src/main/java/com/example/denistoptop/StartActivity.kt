package com.example.denistoptop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.denistoptop.dto.UserDto
import com.example.denistoptop.service.UserService
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class StartActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val user = getUserInfo(this)
        if (user != null) {
            // Идентификатор пользователя доступен, переход к MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрыть текущую активити, чтобы пользователь не мог вернуться к StartActivity
        } else {
            // Идентификатор пользователя не доступен, оставляем StartActivity
            setContentView(R.layout.activity_start)
            // Далее ваша текущая логика
        }

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val login: EditText = findViewById(R.id.login)
            val password: EditText = findViewById(R.id.password)

            val loginText = login.text.toString()
            val passwordText = password.text.toString()

            val jsonUser = "{\"login\": \"$loginText\", \"password\": \"$passwordText\"}"
            val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUser)
            val call = userService.loginUser(requestBody)

            call.enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        // Обработка успешного ответа
                        val userResponse = response.body()
                        // Проверка на успешный вход
                        if (userResponse != null) {
                            // Переход в другое окно
                            // Например:
                            GlobalVariables.userId = userResponse.id
                            GlobalVariables.user = userResponse
                            saveUserInfo(this@StartActivity, GlobalVariables.user)
                            val intent = Intent(this@StartActivity, MainActivity::class.java)
                            startActivity(intent)
                            //Toast.makeText(this@StartActivity, "Всё ок :)", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this@StartActivity, "пздц", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@StartActivity, "пздц2", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    // Обработка ошибок сети или других ошибок
                    Toast.makeText(this@StartActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()

                }
            })
        }

        val registrationButton: Button = findViewById(R.id.registrationButton)
        registrationButton.setOnClickListener {
            // Обработка нажатия на кнопку "Регистрация"
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


    fun saveUserInfo(context: Context, user: UserDto?) {
        val sharedPreferences = context.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(user)
        editor.putString("USER_INFO", userJson)
        editor.apply()

        // Если пользователь предоставлен, отправляем запрос к API для получения полной информации о пользователе и сохраняем ее
        user?.let {
            getUserInfoFromApi(context, it.id.toString())
        }
    }

    fun getUserInfo(context: Context): UserDto? {
        val sharedPreferences = context.getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val gson = Gson()
        val userJson = sharedPreferences.getString("USER_INFO", null)
        return gson.fromJson(userJson, UserDto::class.java)
    }

    private fun getUserInfoFromApi(context: Context, userId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserService::class.java)
        val call = service.getUserById(userId)

        call.enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    // Сохраняем полученного пользователя в SharedPreferences
                    saveUserInfo(context, user)
                } else {
                    // Обработка неуспешного запроса
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
            }
        })
    }
}