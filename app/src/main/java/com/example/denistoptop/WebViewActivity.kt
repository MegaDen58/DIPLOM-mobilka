package com.example.denistoptop

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.denistoptop.dto.UserDto
import com.example.denistoptop.dto.UserManager
import com.example.denistoptop.service.UserService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebViewActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var userService: UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        retrofit = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)

        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        val amount = intent.getIntExtra("EXTRA_AMOUNT", 0)
        val url = "https://yoomoney.ru/quickpay/confirm?receiver=410012789588896&quickpay-form=button&paymentType=AC&sum=${amount}&successURL=myapp://success"

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                return when {
                    url.contains("http://myapp//success") -> {
                        val user = UserManager.getUserInfo(this@WebViewActivity)
                        if (user != null) {
                            updateUserBalance(user.id, amount, this@WebViewActivity)
                        }
                        finish()
                        true
                    }
                    url.contains("http://myapp//failure") -> {
                        // Handle failure
                        finish()
                        true
                    }
                    else -> super.shouldOverrideUrlLoading(view, request)
                }
            }
        }

        webView.loadUrl(url)
    }

    private fun updateUserBalance(userId: Long, amount: Int, context: Context) {
        val jsonUser = "{\"amountToAdd\": \"$amount\"}"
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUser)

        userService.updateUserBalance(userId, requestBody).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Баланс успешно обновлен на ${amount}₽.", Toast.LENGTH_SHORT).show()
                    val updatedUser = response.body()
                    UserManager.saveUserInfo(context, updatedUser)

                    showBalancePopup(context, updatedUser!!.balance)
                } else {
                    Toast.makeText(context, "Не удалось обновить баланс.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(context, "Ошибка при обновлении баланса: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBalancePopup(context: Context, balance: Int) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_layout, null)
        val balanceText = popupView.findViewById<TextView>(R.id.balanceText)
        balanceText.text = "Баланс: ${balance}₽"

        val alertDialog = AlertDialog.Builder(context)
            .setView(popupView)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        alertDialog.show()
    }
}