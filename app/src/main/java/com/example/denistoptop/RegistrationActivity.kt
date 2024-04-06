package com.example.denistoptop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.denistoptop.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Toast
import com.example.denistoptop.dto.UserDto
import okhttp3.MediaType
import okhttp3.RequestBody


class RegistrationActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)

        val registrationButton: Button = findViewById(R.id.registrationButton)
        registrationButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val loginEditText: EditText = findViewById(R.id.login)
        val passwordEditText: EditText = findViewById(R.id.password)
        val emailEditText: EditText = findViewById(R.id.email)

        val login = loginEditText.text.toString()
        val password = passwordEditText.text.toString()
        val email = emailEditText.text.toString()

        val jsonUser = "{\"login\": \"$login\", \"password\": \"$password\", \"email\": \"$email\"}"
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUser)
        val call = userService.registerUser(requestBody)

        call.enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    // Например, показать сообщение об успешной регистрации
                    // и переход на другую активность
                    Toast.makeText(this@RegistrationActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()

                } else {
                    // Обработка неудачного запроса
                    // Например, показать сообщение об ошибке
                    Toast.makeText(this@RegistrationActivity, "Ошибка при регистрации. Пожалуйста, попробуйте еще раз.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
                Toast.makeText(this@RegistrationActivity, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()

            }
        })
    }
}