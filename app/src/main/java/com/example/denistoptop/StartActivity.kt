package com.example.denistoptop

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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class StartActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val login: EditText = findViewById(R.id.login)
            val password: EditText = findViewById(R.id.password)

            val jsonUser = "{\"login\": \"$login\", \"password\": \"$password\"}"
            val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUser)
            val call = userService.registerUser(requestBody)

            call.enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        // Обработка успешного ответа
                        val userResponse = response.body()
                        // Проверка на успешный вход
                        if (userResponse != null) {
                            // Переход в другое окно
                            // Например:
                            // val intent = Intent(this@MainActivity, OtherActivity::class.java)
                            // startActivity(intent)
                            Toast.makeText(this@StartActivity, "Всё ок :)", Toast.LENGTH_SHORT).show()

                        } else {
                            // Пользователь пустой, возможно, неудачный вход
                            // Обработать вход не удался
                        }
                    } else {
                        // Обработка неудачного запроса (например, код ответа не 200)
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    // Обработка ошибок сети или других ошибок
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
}