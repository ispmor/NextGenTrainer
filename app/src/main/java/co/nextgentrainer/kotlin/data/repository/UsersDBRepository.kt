package co.nextgentrainer.kotlin.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import co.nextgentrainer.kotlin.data.model.User
import co.nextgentrainer.kotlin.data.source.UsersDBSource

class UsersDBRepository(val context: Context) {
    fun saveToDB(user: User) {
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
