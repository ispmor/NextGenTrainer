package com.nextgentrainer.kotlin.ui.fitlog.sets

import com.nextgentrainer.kotlin.data.model.ExerciseSet

data class SetsFragmentUiState(
    val isLoading: Boolean = true,
    val sets: List<ExerciseSet> = listOf(),
    val selectedSet: ExerciseSet = ExerciseSet(),
    val userSelectedSet: Boolean = false,
    val userMessages: List<String> = listOf()
)
