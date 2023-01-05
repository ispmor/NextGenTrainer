package com.nextgentrainer.kotlin.ui.fitlog.repetition

import com.nextgentrainer.kotlin.data.model.Repetition

data class RepetitionsFragmentUiState(
    val isLoading: Boolean = true,
    val repetitions: List<Repetition> = listOf(),
    val selectedRepetition: Repetition = Repetition(),
    val userSelectedRepetition: Boolean = false,
    val userMessages: List<String> = listOf()
)
