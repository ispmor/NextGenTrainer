package com.nextgentrainer.kotlin.ui.fitlog

import com.nextgentrainer.kotlin.data.model.Workout

data class FitLogUiState(
    val isLoading: Boolean = true,
    val workoutsItems: List<Workout> = listOf(),
    val userMessages: List<String> = listOf()
)
