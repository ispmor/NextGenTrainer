package com.nextgentrainer.kotlin.data.repository

import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.source.ExerciseSetDataSource

class ExerciseSetRepository(private val exerciseSetDataSource: ExerciseSetDataSource) {
    private var lastExerciseSet: ExerciseSet? = null

    suspend fun saveExerciseSet(exerciseSet: ExerciseSet) {
        exerciseSetDataSource.saveExerciseSet(exerciseSet)
        lastExerciseSet = exerciseSet
    }
}
