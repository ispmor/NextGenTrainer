package com.nextgentrainer.kotlin.posedetector

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.kotlin.VisionProcessorBase
import com.nextgentrainer.kotlin.data.model.Repetition
import com.nextgentrainer.kotlin.data.repository.MovementRepository
import com.nextgentrainer.kotlin.data.repository.RepetitionRepository
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.nextgentrainer.kotlin.graphics.CustomPoseGraphics
import com.nextgentrainer.kotlin.graphics.QualityGraphics
import com.nextgentrainer.kotlin.posedetector.classification.PoseClassifierProcessor
import com.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ExerciseProcessor(
    context: Context,
    options: PoseDetectorOptionsBase?,
    runClassification: Boolean,
    isStreamMode: Boolean,
    private val baseExercise: String,
    private val movementRepository: MovementRepository,
    private val repetitionRepository: RepetitionRepository,
    private var workoutRepository: WorkoutRepository
) : VisionProcessorBase<ExerciseProcessor.PoseWithClassification>(context) {
    var isStarted: Boolean = false
    private val detector: PoseDetector
    private val runClassification: Boolean
    private val isStreamMode: Boolean
    private val context: Context
    private val classificationExecutor: Executor
    var lastQualifiedRepetition: Repetition? = null
    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /**
     * Internal class to hold Pose and classification results.
     */
    class PoseWithClassification(val pose: Pose, val classificationResult: Repetition?)

    init {
        detector = PoseDetection.getClient(options!!)
        this.runClassification = runClassification
        this.isStreamMode = isStreamMode
        this.context = context
        classificationExecutor = Executors.newSingleThreadExecutor()
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage?): Task<PoseWithClassification> {
        return detector
            .process(image!!)
            .continueWith(
                classificationExecutor
            ) { task: Task<Pose> ->
                val pose = task.result
                var classificationResult: Repetition? = repetitionRepository.getEmptyRepetition()
                if (isStarted) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(
                            context,
                            isStreamMode,
                            baseExercise,
                            movementRepository,
                            repetitionRepository
                        )
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)

                    if (Date().time - classificationResult.timestamp.time > 5000) {
                        val setToBeSaved = repetitionRepository.getSet()
                        if (setToBeSaved != null) {
                            workoutRepository.addExerciseSetToWorkout(setToBeSaved)
                            isStarted = false
                            Toast.makeText(context, "Set finished", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                PoseWithClassification(pose, classificationResult)
            }
    }

    override fun detectInImage(image: MlImage?): Task<PoseWithClassification> {
        return detector
            .process(image!!)
            .continueWith(
                classificationExecutor
            ) { task: Task<Pose> ->
                val pose = task.result
                var classificationResult: Repetition? = repetitionRepository.getEmptyRepetition()
                if (isStarted) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(
                            context,
                            isStreamMode,
                            baseExercise,
                            movementRepository,
                            repetitionRepository
                        )
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
                    if (Date().time - classificationResult.timestamp.time > 5000) {
                        val setToBeSaved = repetitionRepository.getSet()
                        if (setToBeSaved != null) {
                            workoutRepository.addExerciseSetToWorkout(setToBeSaved)
                            isStarted = false
                        }
                    }
                }
                PoseWithClassification(pose, classificationResult)
            }
    }

    override fun onSuccess(
        results: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        val oldClassificationResults: MutableList<String> = ArrayList()
        if (results.classificationResult?.repetitionCounter != null) {
            oldClassificationResults.add(
                results.classificationResult.poseName + " " +
                    results.classificationResult.repetitionCounter.numRepeats
            )
        } else {
            oldClassificationResults.add(
                results.classificationResult?.poseName +
                    "No repetitions."
            )
        }
        oldClassificationResults.add(
            results.classificationResult?.confidence
                .toString()
        )

        if (isStarted) {
            graphicOverlay.add(
                CustomPoseGraphics(
                    graphicOverlay,
                    results.pose,
                    results.classificationResult
                )
            )
            if (results.classificationResult?.repetitionCounter != null) {
                graphicOverlay.add(
                    QualityGraphics(
                        graphicOverlay,
                        results.classificationResult
                    )
                )
                lastQualifiedRepetition = results.classificationResult
            } else {
                if (lastQualifiedRepetition != null) {
                    graphicOverlay.add(
                        QualityGraphics(
                            graphicOverlay,
                            results.classificationResult
                        )
                    )
                }
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    override val repetitionCounters: List<RepetitionCounter?>?
        get() = poseClassifierProcessor?.repetitionCountersAsList

    companion object {
        private const val TAG = "ExerciseProcessor"
    }
}
