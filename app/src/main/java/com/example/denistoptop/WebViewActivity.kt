package com.example.denistoptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        val amount = intent.getIntExtra("EXTRA_AMOUNT", 0)
        val url = "https://yoomoney.ru/quickpay/confirm?receiver=410012789588896&quickpay-form=button&paymentType=AC&sum=${amount}&successURL=myapp://success"

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                return when {
                    url.contains("http://myapp//success") -> {
                        finish()
                        Toast.makeText(this@WebViewActivity, "Баланс пополнен на ${amount}₽.", Toast.LENGTH_SHORT).show()
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
}