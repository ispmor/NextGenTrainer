package com.nextgentrainer.kotlin.data.source

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MovementFirebaseSource {
    val database = Firebase.database("https://nextgentrainer-c380e-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Movement")
}
