package com.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class FitLogViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FitLogUiState())
    val uiState: StateFlow<FitLogUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun fetchWorkouts() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val workouts = workoutRepository.getAllWorkouts()
                _uiState.update {
                    it.copy(workoutsItems = workouts)
                }
            } catch (ioe: IOException) {
                // Handle the error and notify the UI when appropriate.
                _uiState.update {
                    val messages = getMessagesFromThrowable(ioe)
                    it.copy(userMessages = messages)
                }
            }
        }
    }

    private fun getMessagesFromThrowable(ioe: IOException): List<String> {
        return listOf(ioe.message!!)
    }
}
