package com.nextgentrainer.kotlin.data.sources

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsersDBSource(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" +
                    USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_COLUMN_NAME + " TEXT, " +
                    USER_COLUMN_AGE + " INT UNSIGNED, " +
                    USER_COLUMN_GENDER + " TEXT" + ")"
        )
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME)
        onCreate(sqLiteDatabase)
    }

    companion object {
        private const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "ngt_database"
        const val USER_TABLE_NAME = "USER"
        const val USER_COLUMN_ID = "_id"
        const val USER_COLUMN_NAME = "name"
        const val USER_COLUMN_AGE = "age"
        const val USER_COLUMN_GENDER = "gender"
    }
}