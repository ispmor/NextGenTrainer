package com.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetsViewModel @Inject constructor(val workoutRepository: WorkoutRepository) : ViewModel() {

    private val _uiState = MutableLiveData<SetsFragmentUiState>()
    val uiState: LiveData<SetsFragmentUiState> = _uiState

    fun setNewSetsList(sets: List<ExerciseSet>) {
        if (sets.isNotEmpty()) {
                _uiState.value = SetsFragmentUiState(
                            sets = sets,
                            isLoading = false
                        )
                    } else {
            _uiState.value = SetsFragmentUiState(userMessages = listOf("Empty sets list!"))
        }
    }

    fun updateSetsListFromRepo() {
        val sets = workoutRepository.selectedWorkout.sets
        val setsAvgQuality = sets.map {
             it.repetitions.map { it.quality!!.quality }.sum() / it.repetitions.size
        }
        val bestIndex = setsAvgQuality.indexOf(setsAvgQuality.max())
        sets[bestIndex].isBest = true
        if (sets.isNotEmpty()) {
            _uiState.value = SetsFragmentUiState(
                sets = sets,
                isLoading = false
            )
        } else {
            _uiState.value = SetsFragmentUiState(userMessages = listOf("Empty sets list!"))
        }
    }
}
