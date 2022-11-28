package com.nextgentrainer.kotlin.data.repository

import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.model.Repetition
import com.nextgentrainer.kotlin.data.model.RepetitionQuality
import com.nextgentrainer.kotlin.data.source.RepetitionFirebaseSource
import com.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date

class RepetitionRepository(private val source: RepetitionFirebaseSource) {
    private val database = source.database

    fun saveRepetition(repetition: Repetition): String {
        val key = database.child(repetition.userId).push().key!!
        database.child(repetition.userId).child(key).setValue(repetition)
        source.addToRepetitionList(repetition)
        return key
    }

    fun getSet(): ExerciseSet? {
        return if (source.getRepetitionList().isNotEmpty()) {
            ExerciseSet(
                source.getRepetitionList()[0].userId,
                source.getRepetitionList()[0].poseName!!,
                source.getRepetitionList()
            )
        } else {
            null
        }
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
        userId: String
    ): Repetition {
        return Repetition(
            maxConfidenceClass,
            confidence,
            repCounter,
            repetitionQuality,
            Date(),
            userId
        )
    }
}
