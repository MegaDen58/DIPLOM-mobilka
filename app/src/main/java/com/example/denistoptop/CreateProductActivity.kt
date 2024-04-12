package com.example.denistoptop

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.ImagesAdapter
import com.example.denistoptop.service.ProductService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.UUID


class CreateProductActivity : AppCompatActivity() {
    private lateinit var galleryButton: Button
    private lateinit var submitButton: Button
    private lateinit var images: MutableList<Uri>
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter
    private lateinit var productService: ProductService
    private val READ_EXTERNAL_STORAGE_REQUEST = 123
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
        requestStoragePermission()
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
            uploadImagesToServer()
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
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST)
        }
    }
    // Проверка результата запроса разрешения
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение получено, теперь вы можете выполнять операции чтения внешнего хранилища
                } else {
                    // Разрешение не получено, обработайте это соответствующим образом, например, показывая диалоговое окно с объяснением
                }
            }
        }
    }
    private fun uploadImagesToServer() {
        for (imageUri in images) {
            val inputStream = contentResolver.openInputStream(imageUri)
            val file = File(cacheDir, "temp_image.png")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
            val body = MultipartBody.Part.createFormData("image", generateRandomFileName(), requestFile)

            val call = productService.uploadImage(body)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()
                        // Делайте что-то с URL изображения, например, сохраняйте его в базу данных или отображайте на экране
                        Toast.makeText(this@CreateProductActivity, "Изображение загружено: $imageUrl", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CreateProductActivity, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@CreateProductActivity, "Ошибка при загрузке изображения: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun generateRandomFileName(): String {
        val uuid = UUID.randomUUID()
        return "${uuid.toString()}.png"
    }
}