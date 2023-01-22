package co.nextgentrainer.kotlin.data.repository

import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.source.ExerciseSetDataSource

class ExerciseSetRepository(private val exerciseSetDataSource: ExerciseSetDataSource) {
    private var lastExerciseSet: ExerciseSet? = null

    suspend fun saveExerciseSet(exerciseSet: ExerciseSet): String {
        val key = exerciseSetDataSource.saveExerciseSet(exerciseSet)
        lastExerciseSet = exerciseSet
        return key
    }
}
