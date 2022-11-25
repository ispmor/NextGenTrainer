package com.nextgentrainer.kotlin.ui.camera

import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.model.Repetition

data class CameraRepetitionState(
    val repetitionList: List<Repetition> = listOf(),
    val exerciseSet: ExerciseSet = ExerciseSet(),
    val setFinished: Boolean = false,
    val userMessage: String = ""
)
