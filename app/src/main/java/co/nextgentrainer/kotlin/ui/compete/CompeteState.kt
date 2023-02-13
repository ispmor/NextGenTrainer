package co.nextgentrainer.kotlin.ui.compete

import android.view.View
import co.nextgentrainer.kotlin.data.model.ExerciseSet

data class CompeteState(
    val exerciseSet: ExerciseSet = ExerciseSet(),
    val setFinished: Boolean = false,
    val userMessage: String = "",
    val startButtonVisibility: Int = View.VISIBLE,
    val imageProcessorIsStarted: Boolean = false,
    val startTimer: Boolean = false,
    val countdownTextViewVisibility: Int = View.INVISIBLE,
    val countDownTextVewText: String = "",
    val challengeRuleTextViewText: String = "",
    val challengeRuleTextViewVisibility: Int = View.INVISIBLE,
    val againstTextViewText: String = "",
    val againstTextViewVisibility: Int = View.INVISIBLE,
    val challengeRuleTextViewTextSize: Float = 1.0f
)
