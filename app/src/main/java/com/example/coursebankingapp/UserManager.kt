package com.example.coursebankingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log

class UserManager(context: Context) {

    private val dbHelper = DBHelper(context)
    private var db: SQLiteDatabase? = null


    fun openDB() {
        db = dbHelper.writableDatabase
    }

    fun closeDB() {
        dbHelper.close()
    }
    fun registerUser(user: User): Long {
        if (isUserExists(user.login)) {
            return -1L // Пользователь уже существует
        }

        val statement = db?.compileStatement(UserObject.getInsertQuery())
        statement?.bindString(1, user.login)
        statement?.bindString(2, user.password)
        statement?.bindString(3, user.name)
        statement?.bindString(4, user.phoneNumber)

        val newId = statement?.executeInsert() ?: -1L
        return newId // Возвращаем сгенерированный ID
    }


    // Проверка наличия пользователя
    fun isUserExists(login: String): Boolean {
        val cursor = db?.rawQuery(UserObject.getCheckUserExistsQuery(), arrayOf(login))
        val exists = cursor?.count ?: 0 > 0
        cursor?.close()
        return exists
    }

    // Авторизация пользователя
    fun authenticateUser(login: String, password: String): User? {
        val cursor = db?.rawQuery(
            UserObject.getAuthenticateUserQuery(),
            arrayOf(login, password)
        )

        // Проверяем, что курсор не пустой
        if (cursor != null && cursor.moveToFirst()) {
            try {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(UserObject.COLUMN_NAME_NAME))
                val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserObject.COLUMN_NAME_PHONE))

                cursor.close()
                return User(id, login, password, name, phoneNumber)
            } catch (e: Exception) {
                Log.e("DBError", "Ошибка извлечения данных пользователя", e)
                cursor.close()
            }
        } else {
            cursor?.close()
            return null
        }
        return null
    }

}