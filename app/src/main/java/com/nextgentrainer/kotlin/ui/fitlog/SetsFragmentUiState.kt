package com.nextgentrainer.kotlin.ui.fitlog

import com.nextgentrainer.kotlin.data.model.ExerciseSet

data class SetsFragmentUiState(
    val isLoading: Boolean = true,
    val sets: List<ExerciseSet> = listOf(),
    val userMessages: List<String> = listOf()
)
