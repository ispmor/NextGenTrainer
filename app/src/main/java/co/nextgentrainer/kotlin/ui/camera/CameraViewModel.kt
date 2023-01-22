package co.nextgentrainer.kotlin.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nextgentrainer.kotlin.data.repository.RepetitionRepository
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class CameraViewModel(
    private val repetitionRepository: RepetitionRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    private val _cameraRepetitionState = MutableLiveData<CameraRepetitionState>()
    val repetitionState: LiveData<CameraRepetitionState> = _cameraRepetitionState

    fun saveExerciseSet() {
        val exerciseSet = repetitionRepository.getSet()
        if (exerciseSet == null) {
            _cameraRepetitionState.value = CameraRepetitionState(userMessage = "No repetitions")
        } else {
            _cameraRepetitionState.value = CameraRepetitionState(exerciseSet = exerciseSet)
            viewModelScope.launch {
                workoutRepository.getAllWorkouts(false)
                workoutRepository.addExerciseSetToWorkout(exerciseSet)
            }
        }
    }

    fun initWorkouts() {
        viewModelScope.launch {
            workoutRepository.initLastWorkout(true)
            // workoutRepository.getAllWorkouts(true)
        }
    }
}
