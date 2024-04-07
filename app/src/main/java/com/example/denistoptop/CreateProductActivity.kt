package com.example.denistoptop

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.ImagesAdapter

class CreateProductActivity : AppCompatActivity() {
    private lateinit var galleryButton: Button
    private lateinit var images: MutableList<Uri>
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
        galleryButton = findViewById(R.id.galleryButton)
        images = mutableListOf()

        adapter = ImagesAdapter(images)
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesRecyclerView.adapter = adapter

        galleryButton.setOnClickListener {
            openGallery()
        }
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

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}