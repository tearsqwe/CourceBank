package com.example.coursebankingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthorizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authorization)

        val userLogin: EditText=findViewById(R.id.ETLogin)
        val userPassword:EditText=findViewById(R.id.ETPassword)
        val tvRegistration: TextView=findViewById(R.id.TVRegister)
        val btnAuth: Button=findViewById(R.id.button_auth)

        btnAuth.setOnClickListener {
            val login = userLogin.text.toString()
            val password = userPassword.text.toString()

            val userManager = UserManager(this)
            userManager.openDB()

            val user = userManager.authenticateUser(login, password)
            if (user != null) {
                Log.d("AuthLog", "Пользователь ${user.name} авторизован, ${user.id}")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", user.name)
                intent.putExtra("USER_ID",user.id)// Передаем имя пользователя
                startActivity(intent)
            } else {
                Log.e("AuthLog", "Ошибка авторизации: Неверный логин или пароль")
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }

            userManager.closeDB()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}