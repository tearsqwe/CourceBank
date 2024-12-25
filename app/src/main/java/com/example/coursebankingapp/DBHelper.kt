package com.example.coursebankingapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context)
    : SQLiteOpenHelper(context, DatabaseInfo.DATABASE_NAME,null, DatabaseInfo.DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query= UserObject.SQL_CREATE_ENTRIES
        db?.execSQL(query)
        val queryCard= CardObject.SQL_CREATE_ENTRIES
        db?.execSQL((queryCard))
        println("onCreate called: Tables created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("onUpgrade called: Upgrading database from version $oldVersion to $newVersion.")
        val query = UserObject.SQL_DELETE_ENTRIES
        db!!.execSQL(query)
        val queryCard=CardObject.SQL_DELETE_ENTRIES
        db!!.execSQL(queryCard)
        onCreate(db)
    }

}