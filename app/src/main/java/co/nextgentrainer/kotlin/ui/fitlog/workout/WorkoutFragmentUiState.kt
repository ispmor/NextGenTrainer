package co.nextgentrainer.kotlin.ui.fitlog.workout

import co.nextgentrainer.kotlin.data.model.Workout

data class WorkoutFragmentUiState(
    val isLoading: Boolean = true,
    val workoutsItems: List<Workout> = listOf(),
    val userMessages: List<String> = listOf(),
    val selectedWorkout: Workout = Workout(),
    val userSelectedWorkout: Boolean = false
)
