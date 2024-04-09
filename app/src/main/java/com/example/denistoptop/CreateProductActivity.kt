package com.example.denistoptop

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.ImagesAdapter
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.ProductService
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateProductActivity : AppCompatActivity() {
    private lateinit var galleryButton: Button
    private lateinit var submitButton: Button
    private lateinit var images: MutableList<Uri>
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter
    private lateinit var productService: ProductService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
        galleryButton = findViewById(R.id.galleryButton)
        submitButton = findViewById(R.id.submitButton)
        images = mutableListOf()

        adapter = ImagesAdapter(images)
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRecyclerView.adapter = adapter

        galleryButton.setOnClickListener {
            openGallery()
        }

        submitButton.setOnClickListener {
            submitProduct()
        }

        productService = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService::class.java)

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    images.add(uri)
                }
            } ?: run {
                val uri = data?.data
                uri?.let {
                    images.add(it)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun submitProduct() {
        // Получение данных о продукте
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val descriptionEditText: EditText = findViewById(R.id.descriptionEditText)
        val countEditText: EditText = findViewById(R.id.countEditText)
        val winterCheckBox: CheckBox = findViewById(R.id.winterCheckBox)
        val summerCheckBox: CheckBox = findViewById(R.id.summerCheckBox)

        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val count = countEditText.text.toString().toInt()
        val winter = winterCheckBox.isChecked
        val summer = summerCheckBox.isChecked

        val productJson = "{\"name\": \"$name\", \"description\": \"$description\", \"count\": $count, \"winter\": $winter, \"summer\": $summer, \"images\": ${images.joinToString(prefix = "[", postfix = "]", separator = ",", transform = { "\"$it\"" })}}"
        val requestBody = RequestBody.create(MediaType.parse("application/json"), productJson)
        val call = productService.createProduct(requestBody)

        call.enqueue(object : Callback<ProductDto> {
            override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа
                    // например, показать сообщение об успешном создании продукта
                    Toast.makeText(this@CreateProductActivity, "Продукт оптравлен!", Toast.LENGTH_SHORT).show()

                } else {
                    // Обработка неудачного запроса
                    // например, показать сообщение об ошибке
                    Toast.makeText(this@CreateProductActivity, "пздц1!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                // Обработка ошибок сети или других ошибок
                // например, показать сообщение об ошибке
                Toast.makeText(this@CreateProductActivity, "пздц2!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}