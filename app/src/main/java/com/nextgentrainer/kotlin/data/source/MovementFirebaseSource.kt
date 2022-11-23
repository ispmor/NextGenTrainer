package com.nextgentrainer.kotlin.data.source

import android.content.Context
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.R

class MovementFirebaseSource(val context: Context) {
    val database = Firebase.database(context.getString(R.string.database_url))
        .getReference(context.getString(R.string.movement))
}
