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
        database.child(repetition.userId).child(repetition.repetitionId).setValue(repetition)
        source.addToRepetitionList(repetition)
        return repetition.repetitionId
    }

    fun getSet(): ExerciseSet? {
        return if (source.getRepetitionList().isNotEmpty()) {
            ExerciseSet(
                source.getRepetitionList()[0].userId,
                source.getRepetitionList()[0].poseName!!.split("_")[0].uppercase(),
                source.getRepetitionList(),
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
            "",
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
        val key = database.child(userId).push().key!!
        return Repetition(
            maxConfidenceClass,
            confidence,
            repCounter,
            repetitionQuality,
            Date(),
            userId,
            key
        )
    }
}
