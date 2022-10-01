package pl.koain.sports.maestry.demo.java.posedetector

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import pl.koain.sports.maestry.demo.GraphicOverlay
import pl.koain.sports.maestry.demo.java.VisionProcessorBase
import pl.koain.sports.maestry.demo.java.posedetector.classification.PoseClassifierProcessor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * A processor to run pose detector.
 */
class PoseDetectorProcessor(
        context: Context,
        options: PoseDetectorOptionsBase?,
        private val showInFrameLikelihood: Boolean,
        private val visualizeZ: Boolean,
        private val rescaleZForVisualization: Boolean,
        runClassification: Boolean,
        isStreamMode: Boolean) : VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>(context) {
    private val detector: PoseDetector
    private val runClassification: Boolean
    private val isStreamMode: Boolean
    private val context: Context
    private val classificationExecutor: Executor
    private var poseClassifierProcessor: PoseClassifierProcessor? = null

    /**
     * Internal class to hold Pose and classification results.
     */
    class PoseWithClassification(val pose: Pose, val classificationResult: List<String?>)

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
                    var classificationResult: List<String?> = ArrayList()
                    if (runClassification) {
                        if (poseClassifierProcessor == null) {
                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode, "")
                        }
                        val oldWay: MutableList<String?> = ArrayList()
                        oldWay.add(poseClassifierProcessor!!.getPoseResult(pose)?.poseName)
                        oldWay.add(poseClassifierProcessor!!.getPoseResult(pose)?.confidence.toString())
                        classificationResult = oldWay // poseClassifierProcessor.getPoseResult(pose);
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
                    var classificationResult: List<String?> = ArrayList()
                    if (runClassification) {
                        if (poseClassifierProcessor == null) {
                            poseClassifierProcessor = PoseClassifierProcessor(context, isStreamMode, "")
                        }
                        val oldWay: MutableList<String?> = ArrayList()
                        oldWay.add(poseClassifierProcessor!!.getPoseResult(pose)?.poseName)
                        oldWay.add(poseClassifierProcessor!!.getPoseResult(pose)?.confidence.toString())
                        classificationResult = oldWay // poseClassifierProcessor.getPoseResult(pose);
                    }
                    PoseWithClassification(pose, classificationResult)
                }
    }

    override fun onSuccess(
            poseWithClassification: PoseWithClassification,
            graphicOverlay: GraphicOverlay) {
        graphicOverlay.add(
                PoseGraphic(
                        graphicOverlay,
                        poseWithClassification.pose,
                        showInFrameLikelihood,
                        visualizeZ,
                        rescaleZForVisualization,
                        poseWithClassification.classificationResult))
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Pose detection failed!", e)
    }

    override fun isMlImageEnabled(context: Context?): Boolean {
        // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
        return true
    }

    companion object {
        private const val TAG = "PoseDetectorProcessor"
    }
}