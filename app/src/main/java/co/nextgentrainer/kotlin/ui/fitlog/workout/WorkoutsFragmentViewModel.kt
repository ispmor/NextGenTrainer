package co.nextgentrainer.kotlin.ui.fitlog.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nextgentrainer.kotlin.data.model.Workout
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WorkoutsFragmentViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<WorkoutFragmentUiState>()
    val uiState: LiveData<WorkoutFragmentUiState> = _uiState

    fun fetchWorkouts() {
        viewModelScope.launch {
            try {
                workoutRepository.getAllUserWorkouts(Firebase.auth.currentUser!!.uid)
                    .addOnSuccessListener {
                        val value = it.getValue<HashMap<String, Workout>>()
                        if (value != null) {
                            val workoutList = value.values.toList().distinctBy { workout -> workout.workoutId }
                            val qualityList = workoutList.map { workout ->
                                workout.sets.flatMap {
                                    it.repetitions.map {
                                        it.quality!!.quality
                                    }
                                }.average()
                            }
                            val bestQuality = qualityList.max()
                            val bestQualityIdx = qualityList.indexOf(bestQuality)
                            val withBestWorkout = workoutList.mapIndexed { i, workout ->
                                if (i == bestQualityIdx) {
                                    Workout(
                                        workout.userId,
                                        workout.workoutId,
                                        workout.timestampMillis,
                                        workout.sets,
                                        true
                                    )
                                } else {
                                    workout
                                }
                            }.sortedWith(compareByDescending { workout -> workout.timestampMillis })

                            workoutRepository.setWorkoutList(workoutList)

                            _uiState.value = WorkoutFragmentUiState(
                                workoutsItems = withBestWorkout,
                                isLoading = false
                            )
                        }
                    }
            } catch (ioe: IOException) {
                // Handle the error and notify the UI when appropriate.
                _uiState.value = WorkoutFragmentUiState(userMessages = getMessagesFromThrowable(ioe))
            }
        }
    }

    private fun getMessagesFromThrowable(ioe: IOException): List<String> {
        return listOf(ioe.message!!)
    }

    fun selectWorkout(workout: Workout) {
        workoutRepository.selectedWorkout = workout
        _uiState.value = WorkoutFragmentUiState(selectedWorkout = workout, userSelectedWorkout = true)
    }
}
