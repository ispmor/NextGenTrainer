package com.nextgentrainer.kotlin.data.repositories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import com.nextgentrainer.kotlin.data.models.User
import com.nextgentrainer.kotlin.data.sources.UsersDBSource


class UsersDBRepository(val context: Context) {



    private fun saveToDB(user: User) {
        val database: SQLiteDatabase = UsersDBSource(context).writableDatabase
        val values = ContentValues()
        values.put(
            UsersDBSource.USER_COLUMN_NAME,
            user.login
        )
        values.put(
            UsersDBSource.USER_COLUMN_AGE,
            user.age
        )
        values.put(
            UsersDBSource.USER_COLUMN_GENDER,
            user.gender
        )
        val newRowId = database.insert(UsersDBSource.USER_TABLE_NAME, null, values)
        Toast.makeText(context, "The new Row Id is $newRowId", Toast.LENGTH_LONG).show()
    }
}