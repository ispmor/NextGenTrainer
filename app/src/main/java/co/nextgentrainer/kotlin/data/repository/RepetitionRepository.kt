package co.nextgentrainer.kotlin.data.repository

import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.model.Repetition
import co.nextgentrainer.kotlin.data.model.RepetitionQuality
import co.nextgentrainer.kotlin.data.source.RepetitionFirebaseSource
import co.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date

class RepetitionRepository(private val source: RepetitionFirebaseSource, val gifRepository: GifRepository) {
    private val database = source.database
    lateinit var selectedRepetition: Repetition

    fun saveRepetition(repetition: Repetition): String {
        database.child(repetition.userId).child(repetition.repetitionId).setValue(repetition)
            .addOnSuccessListener {
                gifRepository.sendPostRequest(repetition.repetitionId, repetition.quality!!.movementId)
            }
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
