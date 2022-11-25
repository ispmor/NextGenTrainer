package com.nextgentrainer.kotlin.data.source

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.kotlin.data.model.Repetition

class RepetitionFirebaseSource {
    val database = Firebase.database("https://nextgentrainer-c380e-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Repetition")

    private val repetitionList: MutableList<Repetition> = mutableListOf()

    fun addToRepetitionList(repetition: Repetition) {
        repetitionList.add(repetition)
    }

    fun getRepetitionList(): List<Repetition> {
        return repetitionList
    }
}
