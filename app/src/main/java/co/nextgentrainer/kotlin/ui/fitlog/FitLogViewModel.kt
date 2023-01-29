package co.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FitLogViewModel @Inject constructor(
    val workoutRepository: WorkoutRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<FitLogUiState>()
    val uiState: LiveData<FitLogUiState> = _uiState
}
