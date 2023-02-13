package co.nextgentrainer.kotlin.ui.fitlog.repetitionAnalysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.nextgentrainer.kotlin.data.model.ChartData
import co.nextgentrainer.kotlin.data.model.Movement
import co.nextgentrainer.kotlin.data.repository.MovementRepository
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RepetitionAnalysisViewModel @Inject constructor(
    val workoutRepository: WorkoutRepository,
    val movementRepository: MovementRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<RepetitionAnalysisFragmentUiState>()
    val uiState: LiveData<RepetitionAnalysisFragmentUiState> = _uiState

    fun updateCharts() {
        val movement: Movement = movementRepository.selectedMovement
        val movementId = movementRepository.selectedMovementId

        val chartsList: MutableList<ChartData> = mutableListOf()

        val smallestDate = movement.timestamps.min()
        val xInSeconds = movement.timestamps.map {
            ((it.time - smallestDate.time) / 1000f)
        }

        if (movement.leftHeelMovement.isNotEmpty() && movement.rightHipMovement.isNotEmpty()) {
            val mockupHeight = 150
            val baseHeight =
                (movement.leftAnkleMovement.maxOf { it.y } + movement.rightAnkleMovement.maxOf { it.y }) / 2
            val topHeight =
                (movement.leftShoulderMovement.minOf { it.y } + movement.rightShoulderMovement.minOf { it.y }) / 2
            val artificialHeightMeasure = mockupHeight / (baseHeight - topHeight)

            val squatDepthLeft =
                movement.leftHipMovement.map { (baseHeight - it.y) * artificialHeightMeasure }
                    .toFloatArray()
            val squatDepthRight =
                movement.rightHipMovement.map { (baseHeight - it.y) * artificialHeightMeasure }
                    .toFloatArray()

            val entries = mutableListOf<Entry>()
            for (i in squatDepthLeft.indices) {
                entries.add(
                    Entry(
                        xInSeconds[i],
                        ((squatDepthLeft[i] + squatDepthRight[i]) / 2)
                    )
                )
            }

            val chartId = "${movementId}_squatDepth"
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Squat Depth")
            chartsList.add(chart)
        }

        if (movement.hipsAngle.isNotEmpty()) {
            val angle = movement.hipsAngle.map { it!!.toFloat() }
            val chartId = "${movementId}_hipsAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart = ChartData(entries, movement.timestamps, chartId, "Hips Angle")
            chartsList.add(chart)
        }

        if (movement.leftKneeAngle.isNotEmpty()) {
            val angle = movement.leftKneeAngle.map { it!!.toFloat() }
            val chartId = "${movementId}_leftKneeAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Left Knee Angle")
            chartsList.add(chart)
        }

        if (movement.rightKneeAngle.isNotEmpty()) {
            val angle = movement.rightKneeAngle.map { it!!.toFloat() }
            val chartId = "${movementId}_rightKneeAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Right Knee Angle")
            chartsList.add(chart)
        }

        if (movement.leftElbowToTorsoAngle.isNotEmpty()) {
            val angle = movement.leftElbowToTorsoAngle.map { it!!.toFloat() }
            val chartId = "${movementId}_leftElbowToTorsoAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Left Armpit Angle")
            chartsList.add(chart)
        }

        if (movement.rightElbowToTorsoAngle.isNotEmpty()) {
            val angle = movement.rightElbowToTorsoAngle.map { it!!.toFloat() }
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chartId = "${movementId}_rightElbowToTorsoAngle"
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Right Armpit Angle")
            chartsList.add(chart)
        }

        if (movement.rightElbowAngle.isNotEmpty()) {
            val angle = movement.rightElbowAngle.map { it.toFloat() }
            val chartId = "${movementId}_rightElbowAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Right Elbow Angle")
            chartsList.add(chart)
        }

        if (movement.leftElbowAngle.isNotEmpty()) {
            val angle = movement.leftElbowAngle.map { it!!.toFloat() }
            val chartId = "${movementId}_leftElbowAngle"
            val entries = mutableListOf<Entry>()
            for (i in xInSeconds.indices) {
                entries.add(Entry(xInSeconds[i], angle[i]))
            }
            val chart =
                ChartData(entries, movement.timestamps, chartId, "Left Elbow Angle")
            chartsList.add(chart)
        }

        _uiState.value = RepetitionAnalysisFragmentUiState(isLoading = false, charts = chartsList)
    }
}
