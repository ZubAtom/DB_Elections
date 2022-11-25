package com.example.db_elections.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.db_elections.view_model.BaseViewModel
import com.example.db_elections.R


class MainActivity : AppCompatActivity() {

    private val viewModel = BaseViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.electionsListRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        viewModel.activityState.createRecycler(this, recyclerView)
    }
}



