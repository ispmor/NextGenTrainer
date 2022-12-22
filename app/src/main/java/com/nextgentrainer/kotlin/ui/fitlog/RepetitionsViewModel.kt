package com.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextgentrainer.kotlin.data.repository.GifRepository
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitionsViewModel @Inject constructor(
    val workoutRepository: WorkoutRepository,
    val gifRepository: GifRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<RepetitionsFragmentUiState>()
    val uiState: LiveData<RepetitionsFragmentUiState> = _uiState

    fun updateRepetitionsListFromRepo() {
        val repetitions = workoutRepository.selectedSet.repetitions
        val repetitionsQuality = repetitions.map { it.quality!!.quality }
        repetitions.forEach {
            viewModelScope.launch {
                it.absoluteLocalPath = gifRepository.downloadImage(it.repetitionId, it.quality!!.movementId)
            }
        }
        repetitions.sortedWith(compareByDescending { rep -> rep.timestamp })

        val bestIndex = repetitionsQuality.indexOf(repetitionsQuality.max())
        repetitions[bestIndex].isBest = true
        if (repetitions.isNotEmpty()) {
            _uiState.value = RepetitionsFragmentUiState(
                repetitions = repetitions,
                isLoading = false
            )
        } else {
            _uiState.value = RepetitionsFragmentUiState(userMessages = listOf("Empty sets list!"))
        }
    }
}
