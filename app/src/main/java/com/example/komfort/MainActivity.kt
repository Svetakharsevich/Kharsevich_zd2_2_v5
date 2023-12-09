package com.example.komfort

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.komfort.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("username", "ects")
        editor.putString("password", "ects")
        editor.apply()


    }
    fun perehod(view: View) {

        val usernameEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.pass)

        val log :String = usernameEditText.text.toString()
        val pas:String = passwordEditText.text.toString()


        if (usernameEditText.text.toString().isEmpty() || passwordEditText.text.toString().isEmpty()) {

            Toast.makeText(this@MainActivity, "Введите логин и пароль", Toast.LENGTH_SHORT).show()
        }
        else {

            val sharedPreferences = getSharedPreferences("LoginAndPassword", MODE_PRIVATE)

            val savedUsername = sharedPreferences.getString("username", "")
            val savedPassword = sharedPreferences.getString("password", "")


            if (log == savedUsername && pas == savedPassword) {
                val intent = Intent(this@MainActivity, MainScreen::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@MainActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()

            }

        }


    }
}