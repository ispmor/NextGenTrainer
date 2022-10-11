package com.nextgentrainer.java.posedetector

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.java.VisionProcessorBase
import com.nextgentrainer.java.graphics.CustomPoseGraphics
import com.nextgentrainer.java.graphics.QualityGraphics
import com.nextgentrainer.java.posedetector.classification.PoseClassifierProcessor
import com.nextgentrainer.java.posedetector.classification.RepetitionCounter
import com.nextgentrainer.java.utils.Repetition
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A processor to run pose detector.
 */
class ExerciseProcessor(
    context: Context,
    options: PoseDetectorOptionsBase?,
    runClassification: Boolean,
    isStreamMode: Boolean,
    private val baseExercise: String
) : VisionProcessorBase<ExerciseProcessor.PoseWithClassification>(context) {
    private val detector: PoseDetector
    private val runClassification: Boolean
    private val isStreamMode: Boolean
    private val context: Context
    private val classificationExecutor: Executor
    private var lastQualifiedRepetition: Repetition? = null
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
                var classificationResult: Repetition? = Repetition()
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode, baseExercise)
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
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
                var classificationResult: Repetition? = Repetition()
                if (runClassification) {
                    if (poseClassifierProcessor == null) {
                        poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode, baseExercise)
                    }
                    classificationResult = poseClassifierProcessor!!.getPoseResult(pose)
                }
                PoseWithClassification(pose, classificationResult)
            }
    }

    override fun onSuccess(
        poseWithClassification: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        val oldClassificationResults: MutableList<String> = ArrayList()
        if (poseWithClassification.classificationResult?.repetitionCounter != null) {
            oldClassificationResults.add(
                poseWithClassification.classificationResult.poseName + " " +
                    poseWithClassification.classificationResult.repetitionCounter.numRepeats
            )
        } else {
            oldClassificationResults.add(
                poseWithClassification.classificationResult?.poseName +
                    "No repetitions."
            )
        }
        oldClassificationResults.add(
            poseWithClassification.classificationResult?.confidence
                .toString()
        )
        graphicOverlay.add(
            CustomPoseGraphics(
                graphicOverlay,
                poseWithClassification.pose,
                oldClassificationResults,
                poseWithClassification.classificationResult
            )
        )
        if (poseWithClassification.classificationResult?.repetitionCounter != null) {
            graphicOverlay.add(
                QualityGraphics(
                    graphicOverlay,
                    poseWithClassification.classificationResult
                )
            )
            lastQualifiedRepetition = poseWithClassification.classificationResult
        } else {
            if (lastQualifiedRepetition != null) {
                graphicOverlay.add(
                    QualityGraphics(
                        graphicOverlay,
                        poseWithClassification.classificationResult
                    )
                )
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
