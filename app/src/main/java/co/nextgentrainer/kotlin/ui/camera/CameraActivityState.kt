package co.nextgentrainer.kotlin.ui.camera

import android.view.View
import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.model.Repetition

data class CameraActivityState(
    val repetitionList: List<Repetition> = listOf(),
    val exerciseSet: ExerciseSet = ExerciseSet(),
    val setFinished: Boolean = false,
    val userMessage: String = "",
    val startButtonVisibility: Int = View.VISIBLE,
    val countDownTextVisibility: Int = View.INVISIBLE,
    val imageProcessorIsStarted: Boolean = false,
    val startTimer: Boolean = false,
    val instructionVisibility: Int = View.VISIBLE
)
