package com.example.komfort

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.example.komfort.databinding.ActivityMainScreenBinding

class MainScreen : Activity() {
    private lateinit var speak: ImageButton
    private lateinit var speak2: ImageButton
    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        speak=findViewById(R.id.discussionsImageView)
        speak2=findViewById(R.id.setsImageView)
        speak.setOnClickListener{
            val intent= Intent(this,Search_map::class.java)
            startActivity(intent)
        }
        speak2.setOnClickListener {
            val intent= Intent(this, List_screen::class.java)
            startActivity(intent)
        }

    }
}