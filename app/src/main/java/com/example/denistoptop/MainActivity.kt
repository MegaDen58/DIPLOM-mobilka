package com.example.denistoptop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.denistoptop.adapter.MainAdapter
import com.example.denistoptop.data.MainData

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myDataset = listOf(
            MainData(R.drawable.ball, "Item 1"),
            MainData(R.drawable.ski, "Item 2"),
            MainData(R.drawable.tent, "Item 3"),
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.adapter = MainAdapter(myDataset)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}