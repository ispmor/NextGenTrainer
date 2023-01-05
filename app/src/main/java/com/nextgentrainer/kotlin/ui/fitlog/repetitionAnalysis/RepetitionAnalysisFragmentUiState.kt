package com.nextgentrainer.kotlin.ui.fitlog.repetitionAnalysis

import com.nextgentrainer.kotlin.data.model.ChartData

data class RepetitionAnalysisFragmentUiState(
    val isLoading: Boolean = true,
    val charts: List<ChartData> = listOf(),
    val userMessages: List<String> = listOf()
)
