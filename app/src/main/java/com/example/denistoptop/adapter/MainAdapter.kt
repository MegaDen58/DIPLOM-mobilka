package com.example.denistoptop.adapter
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.denistoptop.MainItemActivity
import com.example.denistoptop.data.MainData
import com.example.denistoptop.databinding.ItemLayoutBinding
import com.example.denistoptop.dto.ProductDto
import com.example.denistoptop.service.ProductService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainAdapter(private val dataSet: List<MainData>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

        private var listener: OnItemClickListener? = null


    // Объявление интерфейса для обработки нажатий
    interface OnItemClickListener {
        fun onItemClick(imageUrl: String)
    }
    // Метод для установки слушателя нажатий
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    val retrofit = Retrofit.Builder()
        .baseUrl("http://94.228.112.46:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productService = retrofit.create(ProductService::class.java)

    class ViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.binding.textView.text = item.text
        if (item.imageResource.isNotEmpty()) {
            loadImage(item.imageResource, viewHolder.binding.imageView)
        }

        // Обработка нажатий на элемент RecyclerView
        viewHolder.binding.root.setOnClickListener {
            listener?.onItemClick(item.imageResource)

            val picture = item.imageResource.split("/").lastOrNull()
            // Вызов метода getProductByImage и обработка результата
            productService.getProductByImage(picture.toString()).enqueue(object :
                Callback<ProductDto> {
                override fun onResponse(call: Call<ProductDto>, response: Response<ProductDto>) {
                    if (response.isSuccessful) {
                        val product = response.body()
                        val productName = product?.name ?: "ERROR NAME"
                        Log.d("PRODUCT NAME", productName)

                        // Создание Intent для открытия MainItemActivity и передача объекта product
                        val intent = Intent(viewHolder.itemView.context, MainItemActivity::class.java).apply {
                            putExtra("product", product)
                        }
                        viewHolder.itemView.context.startActivity(intent)
                    } else {
                        // Обработка неудачного запроса
                    }
                }

                override fun onFailure(call: Call<ProductDto>, t: Throwable) {
                    // Обработка ошибок сети или других ошибок
                }
            })

            // Вывод URL изображения в логи
            Log.d("MainAdapter", "Clicked on image: ${item.imageResource}")
        }
    }
    private fun loadImage(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
    override fun getItemCount() = dataSet.size
}