package com.example.coursebankingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        val userName=intent.getStringExtra("USER_NAME")?: "Гость"
        val userId=intent.getIntExtra("USER_ID",-1)
        val btnPay: Button=findViewById(R.id.btnPay)

        val tvGreeting: TextView= findViewById(R.id.tvGreeting)
        val tvBalance: TextView = findViewById(R.id.tvBalance)
        tvGreeting.text= "Привет, $userName!, $userId"
        val userManager = UserManager(this)
        userManager.openDB()
        val userBalance = userManager.getUserBalance(userId) // Получаем баланс
        userManager.closeDB()
        tvBalance.text=userBalance.toString()

        btnPay.setOnClickListener{
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}