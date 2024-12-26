package com.example.coursebankingapp

import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActivityCardList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardsAdapter
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        recyclerView = findViewById(R.id.recyclerViewCards)

        dbHelper = DBHelper(this)

        // Получаем все ID пользователей
        val userIds = getAllUserIds()

        // Получаем карты для всех пользователей
        val allCards = mutableListOf<Card>()
        for (userId in userIds) {
            val cards = getCardsForUser(userId)
            Log.d("ActivityCardList", "Получено ${cards.size} карт.")
            allCards.addAll(cards) // Добавляем карты каждого пользователя
            Log.d("ActivityCardList", "В данный момент ${allCards.size}")
        }
        Log.d("ActivityCardList", "Количество карт: ${allCards.size}")

        // Настройка адаптера
        recyclerView.layoutManager=LinearLayoutManager(this)
        cardAdapter = CardsAdapter(allCards)
        recyclerView.adapter = cardAdapter
        cardAdapter.notifyDataSetChanged()


        // Настройка отступов для системы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun getAllUserIds(): List<Long> {
        val db = dbHelper.readableDatabase
        val userIds = mutableListOf<Long>()

        // Запрос для получения всех ID пользователей
        val query = "SELECT ${BaseColumns._ID} FROM ${UserObject.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val userId = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                userIds.add(userId)
            } while (cursor.moveToNext())
        } else {
            Log.d("DatabaseTest", "Запрос не вернул пользователей.")
        }
        cursor.close()
        return userIds
    }

    fun getCardsForUser(userId: Long): List<Card> {
        Log.d("DatabaseTest", "Метод getCardsForUser вызван для пользователя с ID: $userId")
        val cards = mutableListOf<Card>()
        val db = dbHelper.readableDatabase

        // Добавьте фильтрацию по userId в запросе
        val query = """
        SELECT c.${CardObject.COLUMN_NAME_CARD_NUMBER}, u.${UserObject.COLUMN_NAME_NAME} 
        FROM ${CardObject.TABLE_NAME} AS c
        JOIN ${UserObject.TABLE_NAME} AS u 
        ON c.${CardObject.COLUMN_NAME_USER_ID} = u.${BaseColumns._ID}
        WHERE u.${BaseColumns._ID} = ?
    """

        // Подставляем userId в запрос
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        Log.d("DatabaseTest", "Количество карт в запросе: ${cursor.count}")

        if (cursor.moveToFirst()) {
            do {
                val cardNumber = cursor.getLong(cursor.getColumnIndexOrThrow(CardObject.COLUMN_NAME_CARD_NUMBER))
                val userName = cursor.getString(cursor.getColumnIndexOrThrow(UserObject.COLUMN_NAME_NAME))

                Log.d("CardInfo", "Карта: $cardNumber, Владелец: $userName")

                cards.add(Card(cardNumber, userName))
            } while (cursor.moveToNext())
        } else {
            Log.d("DatabaseTest", "Запрос не вернул данных.")
        }
        cursor.close()
        Log.d("DatabaseTest", "Общее количество карт: ${cards.size}")
        return cards
    }


}