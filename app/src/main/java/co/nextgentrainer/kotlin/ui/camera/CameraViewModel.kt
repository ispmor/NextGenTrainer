package co.nextgentrainer.kotlin.ui.camera

import android.app.Application
import android.view.View
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nextgentrainer.GraphicOverlay
import co.nextgentrainer.kotlin.data.repository.MovementRepository
import co.nextgentrainer.kotlin.data.repository.RepetitionRepository
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import co.nextgentrainer.kotlin.utils.CameraActivityHelper.selectModel
import co.nextgentrainer.kotlin.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    val repetitionRepository: RepetitionRepository,
    val workoutRepository: WorkoutRepository,
    val movementRepository: MovementRepository,
    val application: Application
) : ViewModel() {
    private val _cameraViewState = MutableLiveData<CameraActivityState>()
    val cameraViewState: LiveData<CameraActivityState> = _cameraViewState
    var selectedModel = Constants.SQUATS_TRAINER
    var imageProcessor = selectModel(
        selectedModel,
        context = application.applicationContext,
        movementRepository,
        repetitionRepository,
        workoutRepository
    )

    fun saveExerciseSet() {
        val exerciseSet = repetitionRepository.getSet()
        if (exerciseSet == null) {
            _cameraViewState.value = CameraActivityState(userMessage = "No repetitions")
        } else {
            _cameraViewState.value = CameraActivityState(
                exerciseSet = exerciseSet,
                setFinished = true,
                startButtonVisibility = View.VISIBLE
            )
            viewModelScope.launch {
                workoutRepository.getAllWorkouts(false)
                workoutRepository.addExerciseSetToWorkout(exerciseSet)
            }
        }
    }

    fun initWorkouts() {
        viewModelScope.launch {
            workoutRepository.initLastWorkout(true)
        }
    }

    fun saveRecording(exerciseName: String) {
        imageProcessor.poseClassifierProcessor?.saveRecording(exerciseName.uppercase())
    }

    fun startExerciseCountDown() {
        _cameraViewState.value = CameraActivityState(
            startButtonVisibility = View.INVISIBLE,
            countDownTextVisibility = View.VISIBLE,
            startTimer = true
        )

        imageProcessor.setIsProcessing(false)
    }

    fun startMovementProcessing() {
        _cameraViewState.value = CameraActivityState(
            startButtonVisibility = View.INVISIBLE,
            countDownTextVisibility = View.INVISIBLE
        )
        imageProcessor.setIsProcessing(true)

    }

    fun resetToDefaultState() {
        _cameraViewState.value = CameraActivityState()
        imageProcessor.setIsProcessing(false)
    }

    fun stop() {
        imageProcessor.stop()
    }

    @androidx.camera.core.ExperimentalGetImage
    fun processImageProxy(imageProxy: ImageProxy, graphicOverlay: GraphicOverlay){
        imageProcessor.processImageProxy(imageProxy, graphicOverlay)
    }

    fun getImageProcessor() {
        imageProcessor = selectModel(
            selectedModel,
            application.applicationContext,
            movementRepository,
            repetitionRepository,
            workoutRepository
        )
    }
}
