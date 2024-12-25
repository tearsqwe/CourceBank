package com.example.coursebankingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CardActivity : AppCompatActivity() {

    private val dbHelper = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        val baseCard: TextView = findViewById(R.id.tvBase)
        val advancedCard: TextView = findViewById(R.id.tvAdvanced)
        val premiumCard: TextView = findViewById(R.id.tvPremium)

        // Получаем ID пользователя из Intent
        val userId = intent.getLongExtra("USER_ID", -1L)
        if (userId == -1L) {
            Toast.makeText(this, "Ошибка получения ID пользователя", Toast.LENGTH_SHORT).show()
            finish()  // Завершаем Activity, если ID не передан
            return
        }

        // Обработчики нажатий на карточки
        baseCard.setOnClickListener {
            createCard(userId, "Base")
        }

        advancedCard.setOnClickListener {
            createCard(userId, "Advanced")
        }

        premiumCard.setOnClickListener {
            createCard(userId, "Premium")
        }

        // Для правильного отображения на устройствах с вырезами и системными панелями
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createCard(userId: Long, cardType: String) {
        val db = dbHelper.writableDatabase

        // Проверка наличия карт для пользователя
        val cursor = db.rawQuery(
            "SELECT * FROM ${CardObject.TABLE_NAME} WHERE ${CardObject.COLUMN_NAME_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            // Если карта уже существует для этого пользователя
            Toast.makeText(this, "У пользователя уже есть карта", Toast.LENGTH_SHORT).show()
            cursor.close()
            return
        }
        cursor.close()

        // Получаем следующий номер карты
        val nextCardNumber = getNextCardNumber()

        // Вставляем новую карту
        val insertQuery = "INSERT INTO ${CardObject.TABLE_NAME} (${CardObject.COLUMN_NAME_USER_ID}, ${CardObject.COLUMN_NAME_CARD_NUMBER}, ${CardObject.COLUMN_NAME_CARD_TYPE}, ${CardObject.COLUMN_NAME_BALANCE}) VALUES (?, ?, ?, ?)"
        val statement = db.compileStatement(insertQuery)
        statement.bindLong(1, userId) // связываем пользователя с картой
        statement.bindLong(2, nextCardNumber)  // номер карты
        statement.bindString(3, cardType)     // тип карты
        statement.bindDouble(4, 10000.0)      // баланс по умолчанию
        val rowId=statement.executeInsert()
        Log.d("CardActivity", "Создана карта с ID: $rowId, номер карты: $nextCardNumber, тип карты: $cardType")

        Toast.makeText(this, "Карта типа $cardType создана!", Toast.LENGTH_SHORT).show()

        // Переходим на экран авторизации после создания карты
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()  // Закрываем текущую активность
    }

    private fun getNextCardNumber(): Long {
        // Получаем следующий номер карты. В этом примере предполагаем, что карты пронумерованы
        // последовательно, и следующий номер равен максимальному номеру + 1.
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT MAX(${CardObject.COLUMN_NAME_CARD_NUMBER}) FROM ${CardObject.TABLE_NAME}", null)
        val nextCardNumber = if (cursor.moveToFirst()) {
            cursor.getLong(0) + 1
        } else {
            1 // Начинаем с 1, если в базе нет карт
        }
        cursor.close()
        return nextCardNumber
    }
}
