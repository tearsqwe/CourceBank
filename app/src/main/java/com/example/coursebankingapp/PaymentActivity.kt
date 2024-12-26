package com.example.coursebankingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        val etID = findViewById<EditText>(R.id.etID)
        val etSum = findViewById<EditText>(R.id.etSum)
        val btnTransfer = findViewById<Button>(R.id.btnTransfer)

        val userManager = UserManager(this)
        userManager.openDB()

        btnTransfer.setOnClickListener {
            val currentId= intent.getIntExtra("USER_ID",-1)
            Log.d(" currentId", "$currentId")
            val recipientId = etID.text.toString().toIntOrNull()
            val transferAmount = etSum.text.toString().toDoubleOrNull()

            if (recipientId == null || transferAmount == null || transferAmount <= 0) {
                Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentId == -1) {
                Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, существует ли получатель
            if (!userManager.isUserExistsById(recipientId)) {
                Toast.makeText(this, "Получатель не найден", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Выполняем перевод
            val success = userManager.transferFunds(currentId, recipientId, transferAmount)
            if (success) {
                Toast.makeText(this, "Перевод выполнен успешно", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Недостаточно средств для перевода", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
