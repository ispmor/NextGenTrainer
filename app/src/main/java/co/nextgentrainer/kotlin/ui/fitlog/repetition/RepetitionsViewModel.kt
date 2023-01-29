package co.nextgentrainer.kotlin.ui.fitlog.repetition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nextgentrainer.kotlin.data.model.Movement
import co.nextgentrainer.kotlin.data.model.Repetition
import co.nextgentrainer.kotlin.data.repository.GifRepository
import co.nextgentrainer.kotlin.data.repository.MovementRepository
import co.nextgentrainer.kotlin.data.repository.RepetitionRepository
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepetitionsViewModel @Inject constructor(
    val workoutRepository: WorkoutRepository,
    val gifRepository: GifRepository,
    val repetitionRepository: RepetitionRepository,
    val movementRepository: MovementRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<RepetitionsFragmentUiState>()
    val uiState: LiveData<RepetitionsFragmentUiState> = _uiState

    fun updateRepetitionsListFromRepo() {
        val repetitions = workoutRepository.selectedSet.repetitions
        val repetitionsQuality = repetitions.map { it.quality!!.quality }
        repetitions.forEach {
            viewModelScope.launch {
                it.absoluteLocalPath =
                    gifRepository.downloadImage(it.repetitionId, it.quality!!.movementId)
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

    fun selectRepetition(repetition: Repetition) {
        viewModelScope.launch {
            repetitionRepository.selectedRepetition = repetition
            val movementId = repetition.quality!!.movementId
            movementRepository.getMovement(movementId).addOnSuccessListener {
                val value = it.getValue<Movement>()
                if (value != null) {
                    movementRepository.selectedMovement = value
                    movementRepository.selectedMovementId = it.key!!

                    _uiState.value = RepetitionsFragmentUiState(
                        selectedRepetition = repetition,
                        userSelectedRepetition = true
                    )
                }
            }
        }
    }
}
