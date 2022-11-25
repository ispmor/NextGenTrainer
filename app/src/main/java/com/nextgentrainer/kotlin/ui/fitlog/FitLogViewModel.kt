package com.nextgentrainer.kotlin.ui.fitlog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.kotlin.data.model.Workout
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FitLogViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    ) : ViewModel() {

    private val _uiState = MutableLiveData<FitLogUiState>()
    val uiState: LiveData<FitLogUiState> = _uiState

    fun fetchWorkouts() {
        viewModelScope.launch {
            try {
                 workoutRepository.getAllUserWorkouts(Firebase.auth.currentUser!!.uid).addOnSuccessListener {
                     val value = it.getValue<HashMap<String, Workout>>()
                     if (value != null) {
                         _uiState.value = FitLogUiState(workoutsItems = value.values.toList(), isLoading = false)
                     }
                 }
            } catch (ioe: IOException) {
                // Handle the error and notify the UI when appropriate.
              _uiState.value = FitLogUiState(userMessages = getMessagesFromThrowable(ioe))
                }
            }
        }

    private fun getMessagesFromThrowable(ioe: IOException): List<String> {
        return listOf(ioe.message!!)
    }
}
