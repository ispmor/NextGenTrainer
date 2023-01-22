package co.nextgentrainer.kotlin.ui.camera

import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.model.Repetition

data class CameraRepetitionState(
    val repetitionList: List<Repetition> = listOf(),
    val exerciseSet: ExerciseSet = ExerciseSet(),
    val setFinished: Boolean = false,
    val userMessage: String = ""
)
