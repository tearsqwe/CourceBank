package com.example.coursebankingapp

import android.provider.BaseColumns

object UserObject : BaseColumns {
    const val TABLE_NAME = "Users"
    const val COLUMN_NAME_LOGIN = "Login"
    const val COLUMN_NAME_PASSWORD = "Password"
    const val COLUMN_NAME_NAME = "Name"
    const val COLUMN_NAME_PHONE = "Phone" // Новый столбец для номера телефона

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_LOGIN TEXT NOT NULL," +
                "$COLUMN_NAME_PASSWORD TEXT NOT NULL," +
                "$COLUMN_NAME_NAME TEXT NOT NULL," +
                "$COLUMN_NAME_PHONE TEXT NOT NULL)" // Добавление нового столбца

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

    fun getInsertQuery(): String {
        return "INSERT INTO $TABLE_NAME ($COLUMN_NAME_LOGIN, $COLUMN_NAME_PASSWORD, $COLUMN_NAME_NAME, $COLUMN_NAME_PHONE) VALUES (?, ?, ?, ?)"
    }
    fun getCheckUserExistsQuery(): String {
        return "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_LOGIN = ?"
    }
    fun getAuthenticateUserQuery(): String {
        return "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_LOGIN = ? AND $COLUMN_NAME_PASSWORD = ?"
    }
}