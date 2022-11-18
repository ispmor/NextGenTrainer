package com.nextgentrainer.kotlin.data.repositories

import android.content.Context
import com.nextgentrainer.kotlin.data.models.Repetition
import com.nextgentrainer.kotlin.data.models.RepetitionQuality
import com.nextgentrainer.kotlin.data.sources.RepetitionFirebaseSource
import com.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date

class RepetitionRepository(val context: Context) {
    private val database = RepetitionFirebaseSource(context).database

    fun saveRepetition(repetition: Repetition): String {
        val key = database.push().key!!
        database.child(key).setValue(repetition)
        return key
    }

    fun getEmptyRepetition(): Repetition {
        return Repetition(
            "",
            0.0F,
            null,
            null,
            Date(),
            ""
        )
    }

    fun createRepetition(
        maxConfidenceClass: String,
        confidence: Float,
        repCounter: RepetitionCounter,
        repetitionQuality: RepetitionQuality,
        userLogin: String
    ): Repetition {
        return Repetition(
            maxConfidenceClass,
            confidence,
            repCounter,
            repetitionQuality,
            Date(),
            userLogin
        )
    }
}
