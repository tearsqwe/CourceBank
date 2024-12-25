package com.example.coursebankingapp

import android.provider.BaseColumns

object CardObject : BaseColumns {
    const val TABLE_NAME = "Cards"
    const val COLUMN_NAME_CARD_NUMBER = "CardNumber"
    const val COLUMN_NAME_CARD_TYPE = "CardType"
    const val COLUMN_NAME_BALANCE = "Balance"
    const val COLUMN_NAME_USER_ID = "UserId" // Связь с таблицей Users

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_CARD_NUMBER INTEGER NOT NULL UNIQUE," +
                "$COLUMN_NAME_CARD_TYPE TEXT NOT NULL," +
                "$COLUMN_NAME_BALANCE REAL NOT NULL," +
                "$COLUMN_NAME_USER_ID INTEGER NOT NULL," +
                "FOREIGN KEY($COLUMN_NAME_USER_ID) REFERENCES ${UserObject.TABLE_NAME}(${BaseColumns._ID}) ON DELETE CASCADE)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    fun getInsertQuery(): String {
        return "INSERT INTO $TABLE_NAME ($COLUMN_NAME_CARD_NUMBER, $COLUMN_NAME_CARD_TYPE, $COLUMN_NAME_BALANCE, $COLUMN_NAME_USER_ID) VALUES (?, ?, ?, ?)"
    }

    fun getUserCardsQuery(): String {
        return "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_USER_ID = ?"
    }
    fun getNextCardNumberQuery(): String {
        return "SELECT MAX($COLUMN_NAME_CARD_NUMBER) FROM $TABLE_NAME"
    }
}
