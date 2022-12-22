package com.nextgentrainer.kotlin.ui.fitlog

import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.model.Repetition

data class RepetitionsFragmentUiState(
    val isLoading: Boolean = true,
    val repetitions: List<Repetition> = listOf(),
    val selectedSet: ExerciseSet = ExerciseSet(),
    val userSelectedRepetition: Boolean = false,
    val userMessages: List<String> = listOf()
)
