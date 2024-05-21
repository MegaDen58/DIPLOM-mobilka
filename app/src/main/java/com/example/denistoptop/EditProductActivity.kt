package com.example.denistoptop

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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

class EditProductActivity : AppCompatActivity() {

    private lateinit var galleryButton: Button
    private lateinit var submitButton: Button
    private lateinit var clearButton: Button
    private lateinit var clearImagesButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var colorEditText: EditText
    private lateinit var sizeEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var materialEditText: EditText
    private lateinit var winterCheckBox: CheckBox
    private lateinit var summerCheckBox: CheckBox
    private lateinit var images: MutableList<Uri>
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter
    private lateinit var productService: ProductService
    private val READ_EXTERNAL_STORAGE_REQUEST = 123
    private var imagesName: MutableList<String> = mutableListOf()
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val product: ProductDto? = intent.getSerializableExtra("editProduct") as? ProductDto

        Log.w("ITEM", product.toString())

        productService = Retrofit.Builder()
            .baseUrl("http://94.228.112.46:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductService::class.java)

        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
        galleryButton = findViewById(R.id.galleryButton)
        submitButton = findViewById(R.id.submitButton)
        clearButton = findViewById(R.id.clearButton)
        images = mutableListOf()

        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        colorEditText = findViewById(R.id.colorEditText)
        sizeEditText = findViewById(R.id.sizeEditText)
        priceEditText = findViewById(R.id.priceEditText)
        materialEditText = findViewById(R.id.materialEditText)
        winterCheckBox = findViewById(R.id.winterCheckBox)
        summerCheckBox = findViewById(R.id.summerCheckBox)
        clearImagesButton = findViewById(R.id.clearImagesButton)

        clearImagesButton.setOnClickListener {
            images.clear()  // Очистить список изображений
            adapter.notifyDataSetChanged()  // Уведомить адаптер об изменении данных
        }

        clearButton.setOnClickListener {
            images.clear()
            adapter.notifyDataSetChanged()

            nameEditText.setText("")
            descriptionEditText.setText("")
            colorEditText.setText("")
            sizeEditText.setText("")
            priceEditText.setText("")
            materialEditText.setText("")

            winterCheckBox.isChecked = false
            summerCheckBox.isChecked = false
        }

        submitButton.setOnClickListener {
            sendEditProductRequest()
        }

        galleryButton.setOnClickListener {
            openGallery()
        }

        nameEditText.setText(product?.name)
        descriptionEditText.setText(product?.description)
        colorEditText.setText(product?.color)
        materialEditText.setText(product?.material)
        sizeEditText.setText(product?.size)
        priceEditText.setText(product?.price.toString())

        if(product?.isSummer == true){
            summerCheckBox.isChecked = true
        }
        if(product?.isWinter == true){
            winterCheckBox.isChecked = true
        }

        val imageUrls = product?.images?.map { imageName -> "http://94.228.112.46:8080/api/products/image/$imageName" } ?: emptyList()
        // Преобразование строковых URL в Uri и добавление в список images
        val imageUris = imageUrls.map { url -> Uri.parse(url) }
        images.addAll(imageUris)

        // Установка адаптера для RecyclerView
        adapter = ImagesAdapter(images)
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRecyclerView.adapter = adapter
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            CreateProductActivity.PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CreateProductActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
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

    // Проверка результата запроса  разрешения
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


    private fun sendEditProductRequest() {
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val color = colorEditText.text.toString()
        val size = sizeEditText.text.toString()
        val material = materialEditText.text.toString()
        val price = priceEditText.text.toString().toIntOrNull() ?: 0
        val isWinter = winterCheckBox.isChecked
        val isSummer = summerCheckBox.isChecked

        val product = intent.getSerializableExtra("editProduct") as? ProductDto
        val existingImages = product?.images?.toMutableList() ?: mutableListOf()

        // Добавление имен новых изображений к списку существующих
        existingImages.addAll(imagesName)

        // Преобразование списка изображений в JSON массив
        val imagesList = existingImages.map { "\"$it\"" }
        val imagesJsonArray = imagesList.joinToString(prefix = "[", postfix = "]")

        val requestBody = """
        {
            "name": "$name",
            "description": "$description",
            "color": "$color",
            "price": $price,
            "winter": $isWinter,
            "summer": $isSummer,
            "size": "$size",
            "material": "$material",
            "images": $imagesJsonArray
        }
    """.trimIndent()

        Log.w("TAG", requestBody)
        val request = RequestBody.create(MediaType.parse("application/json"), requestBody)
        val productId = (intent.getSerializableExtra("editProduct") as? ProductDto)?.id ?: return
        val call = productService.updateProduct(productId.toString(), request)

        call.enqueue(object : Callback<ProductDto> {
            override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Товар обновлен!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Неудачный запрос.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}