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
    fun isUserExistsById(userId: Int): Boolean {
        val cursor = db?.rawQuery("SELECT * FROM ${UserObject.TABLE_NAME} WHERE ${BaseColumns._ID} = ?", arrayOf(userId.toString()))
        val exists = cursor?.count ?: 0 > 0
        cursor?.close()
        return exists
    }

    fun transferFunds(senderId: Int, recipientId: Int, amount: Double): Boolean {
        db?.beginTransaction()
        try {
            // Получаем текущий баланс отправителя
            val senderCursor = db?.rawQuery(
                "SELECT ${CardObject.COLUMN_NAME_BALANCE} FROM ${CardObject.TABLE_NAME} WHERE ${CardObject.COLUMN_NAME_USER_ID} = ?",
                arrayOf(senderId.toString())
            )
            if (senderCursor?.moveToFirst() == true) {
                val senderBalance = senderCursor.getDouble(senderCursor.getColumnIndexOrThrow(CardObject.COLUMN_NAME_BALANCE))
                if (senderBalance >= amount) {
                    // Обновляем баланс отправителя
                    db?.execSQL(
                        "UPDATE ${CardObject.TABLE_NAME} SET ${CardObject.COLUMN_NAME_BALANCE} = ${CardObject.COLUMN_NAME_BALANCE} - ? WHERE ${CardObject.COLUMN_NAME_USER_ID} = ?",
                        arrayOf(amount.toString(), senderId.toString())
                    )
                    // Обновляем баланс получателя
                    db?.execSQL(
                        "UPDATE ${CardObject.TABLE_NAME} SET ${CardObject.COLUMN_NAME_BALANCE} = ${CardObject.COLUMN_NAME_BALANCE} + ? WHERE ${CardObject.COLUMN_NAME_USER_ID} = ?",
                        arrayOf(amount.toString(), recipientId.toString())
                    )
                    db?.setTransactionSuccessful()
                    return true
                }
            }
            senderCursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.endTransaction()
        }
        return false
    }
    fun getUserBalance(userId: Int): Double {
        val cursor = db?.rawQuery("SELECT SUM(${CardObject.COLUMN_NAME_BALANCE}) FROM ${CardObject.TABLE_NAME} WHERE ${CardObject.COLUMN_NAME_USER_ID} = ?", arrayOf(userId.toString()))
        cursor?.moveToFirst()
        val balance = cursor?.getDouble(0) ?: 0.0
        cursor?.close()
        return balance
    }

}