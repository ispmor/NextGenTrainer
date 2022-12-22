package com.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgentrainer.kotlin.data.model.ExerciseSet
import com.nextgentrainer.kotlin.data.repository.GifRepository
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
        val setsNotSorted = workoutRepository.selectedWorkout.sets
        val setsAvgQuality = setsNotSorted.map {
             it.repetitions.map { it.quality!!.quality }.sum() / it.repetitions.size
        }
        val bestIndex = setsAvgQuality.indexOf(setsAvgQuality.max())
        setsNotSorted[bestIndex].isBest = true
        val sets = setsNotSorted.sortedWith(compareByDescending { set -> set.repetitions[0].timestamp  })
        if (sets.isNotEmpty()) {
            _uiState.value = SetsFragmentUiState(
                sets = sets,
                isLoading = false
            )
        } else {
            _uiState.value = SetsFragmentUiState(userMessages = listOf("Empty sets list!"))
        }
    }

    fun selectSet(set: ExerciseSet) {
        workoutRepository.selectedSet = set
        _uiState.value = SetsFragmentUiState(selectedSet = set, userSelectedSet = true)
    }
}
