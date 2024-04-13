package com.example.denistoptop.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.denistoptop.data.MainData
import com.example.denistoptop.databinding.ItemLayoutBinding

class MainAdapter(private val dataSet: List<MainData>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

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
    }
    private fun loadImage(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
    override fun getItemCount() = dataSet.size
}