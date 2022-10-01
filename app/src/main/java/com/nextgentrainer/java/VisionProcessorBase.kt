package com.nextgentrainer.java

import com.nextgentrainer.java.posedetector.classification.RepetitionCounter
import com.nextgentrainer.java.utils.RepetitionQuality
import com.nextgentrainer.java.utils.Repetition
import com.google.mlkit.vision.common.PointF3D
import com.nextgentrainer.java.utils.LineEquation
import com.google.mlkit.vision.pose.Pose
import com.nextgentrainer.java.utils.QualityFeature
import com.nextgentrainer.java.posedetector.MovementDescription
import com.nextgentrainer.java.utils.QualityDetector
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.GraphicOverlay.Graphic
import com.nextgentrainer.java.graphics.QualityGraphics
import com.google.common.primitives.Floats
import com.nextgentrainer.java.posedetector.classification.PoseEmbedding
import com.nextgentrainer.java.posedetector.classification.PoseSample
import kotlin.jvm.JvmOverloads
import com.nextgentrainer.java.posedetector.classification.EMASmoothing
import com.nextgentrainer.java.posedetector.classification.ClassificationResult
import com.google.mlkit.vision.pose.PoseLandmark
import com.nextgentrainer.java.posedetector.classification.PoseClassifier
import android.media.MediaPlayer
import com.nextgentrainer.java.utils.ExerciseSet
import com.nextgentrainer.R
import com.nextgentrainer.java.posedetector.classification.PoseClassifierProcessor
import com.google.gson.Gson
import com.nextgentrainer.java.posedetector.PoseGraphic
import com.google.common.primitives.Ints
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.nextgentrainer.java.VisionProcessorBase
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.common.InputImage
import com.google.android.odml.image.MlImage
import com.nextgentrainer.java.graphics.CustomPoseGraphics
import com.nextgentrainer.java.posedetector.ExerciseProcessor
import com.nextgentrainer.java.posedetector.PoseDetectorProcessor
import com.google.android.gms.common.annotation.KeepName
import androidx.annotation.RequiresApi
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageAnalysis
import com.nextgentrainer.VisionImageProcessor
import com.nextgentrainer.java.CameraActivity
import androidx.camera.core.CameraSelector
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.nextgentrainer.CameraXViewModel
import android.content.Intent
import com.nextgentrainer.preference.SettingsActivity
import com.nextgentrainer.preference.SettingsActivity.LaunchSource
import android.widget.AdapterView
import androidx.camera.core.CameraInfoUnavailableException
import android.widget.Toast
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.MlKitException
import android.app.Activity
import com.nextgentrainer.java.FitLogActivity
import com.nextgentrainer.java.FitLogActivity.MyArrayAdapter
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.google.gson.GsonBuilder
import com.google.gson.JsonStreamParser
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import android.widget.AdapterView.OnItemClickListener
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import com.nextgentrainer.java.ChooserActivity
import android.view.WindowManager
import android.app.ActivityManager
import com.nextgentrainer.ScopedExecutor
import com.nextgentrainer.TemperatureMonitor
import com.nextgentrainer.FrameMetadata
import android.graphics.Bitmap
import com.google.android.odml.image.BitmapMlImageBuilder
import com.nextgentrainer.BitmapUtils
import com.google.android.odml.image.ByteBufferMlImageBuilder
import androidx.camera.core.ExperimentalGetImage
import com.google.android.odml.image.MediaMlImageBuilder
import com.nextgentrainer.CameraImageGraphic
import com.nextgentrainer.InferenceInfoGraphic
import androidx.annotation.StringRes
import android.preference.PreferenceManager
import com.nextgentrainer.CameraSource.SizePair
import com.nextgentrainer.CameraSource
import android.content.SharedPreferences
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import android.preference.PreferenceFragment
import com.nextgentrainer.preference.CameraXLivePreviewPreferenceFragment
import android.preference.PreferenceCategory
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import com.nextgentrainer.preference.LivePreviewPreferenceFragment
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraAccessException
import android.graphics.YuvImage
import android.graphics.BitmapFactory
import kotlin.Throws
import android.content.ContentResolver
import android.provider.MediaStore
import android.media.Image.Plane
import android.annotation.TargetApi
import com.nextgentrainer.CameraSource.FrameProcessingRunnable
import androidx.annotation.RequiresPermission
import android.view.SurfaceHolder
import android.annotation.SuppressLint
import android.content.Context
import com.nextgentrainer.CameraSource.CameraPreviewCallback
import android.hardware.Camera.PreviewCallback
import android.view.View.OnLayoutChangeListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.os.*
import android.util.Log
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.*
import com.nextgentrainer.preference.PreferenceUtils
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*

/**
 * Abstract base class for vision frame processors. Subclasses need to implement [ ][.onSuccess] to define what they want to with the detection results and
 * [.detectInImage] to specify the detector object.
 *
 * @param <T> The type of the detected feature.
</T> */
abstract class VisionProcessorBase<T> protected constructor(context: Context) : VisionImageProcessor {
    private val activityManager: ActivityManager
    private val fpsTimer = Timer()
    private val executor: ScopedExecutor
    private val temperatureMonitor: TemperatureMonitor

    // Whether this processor is already shut down
    private var isShutdown = false

    // Used to calculate latency, running in the same thread, no sync needed.
    private var numRuns = 0
    private var totalFrameMs: Long = 0
    private var maxFrameMs: Long = 0
    private var minFrameMs = Long.MAX_VALUE
    private var totalDetectorMs: Long = 0
    private var maxDetectorMs: Long = 0
    private var minDetectorMs = Long.MAX_VALUE

    // Frame count that have been processed so far in an one second interval to calculate FPS.
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0
    open val repetitionCounters: List<RepetitionCounter?>? = null

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    init {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)
        fpsTimer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        framesPerSecond = frameProcessedInOneSecondInterval
                        frameProcessedInOneSecondInterval = 0
                    }
                },  /* delay= */
                0,  /* period= */
                1000)
        temperatureMonitor = TemperatureMonitor(context)
    }

    // -----------------Code for processing single still image----------------------------------------
    override fun processBitmap(bitmap: Bitmap?, graphicOverlay: GraphicOverlay) {
        val frameStartMs = SystemClock.elapsedRealtime()
        if (isMlImageEnabled(graphicOverlay.context)) {
            val mlImage = BitmapMlImageBuilder(bitmap!!).build()
            requestDetectInImage(
                    mlImage,
                    graphicOverlay,  /* originalCameraImage= */
                    null,  /* shouldShowFps= */
                    false,
                    frameStartMs)
            mlImage.close()
            return
        }
        requestDetectInImage(
                InputImage.fromBitmap(bitmap!!, 0),
                graphicOverlay,  /* originalCameraImage= */
                null,  /* shouldShowFps= */
                false,
                frameStartMs)
    }

    // -----------------Code for processing live preview frame from Camera1 API-----------------------
    @Synchronized
    override fun processByteBuffer(
            data: ByteBuffer?, frameMetadata: FrameMetadata?, graphicOverlay: GraphicOverlay) {
        latestImage = data
        latestImageMetaData = frameMetadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay)
        }
    }

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage!!, processingMetaData!!, graphicOverlay)
        }
    }

    private fun processImage(
            data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        val frameStartMs = SystemClock.elapsedRealtime()

        // If live viewport is on (that is the underneath surface view takes care of the camera preview
        // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
        val bitmap = if (PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.context)) null else BitmapUtils.getBitmap(data, frameMetadata)
        if (isMlImageEnabled(graphicOverlay.context)) {
            val mlImage = ByteBufferMlImageBuilder(
                    data,
                    frameMetadata.width,
                    frameMetadata.height,
                    MlImage.IMAGE_FORMAT_NV21)
                    .setRotation(frameMetadata.rotation)
                    .build()
            requestDetectInImage(mlImage, graphicOverlay, bitmap,  /* shouldShowFps= */true, frameStartMs)
                    .addOnSuccessListener(executor) { results: T -> processLatestImage(graphicOverlay) }

            // This is optional. Java Garbage collection can also close it eventually.
            mlImage.close()
            return
        }
        requestDetectInImage(
                InputImage.fromByteBuffer(
                        data,
                        frameMetadata.width,
                        frameMetadata.height,
                        frameMetadata.rotation,
                        InputImage.IMAGE_FORMAT_NV21),
                graphicOverlay,
                bitmap,  /* shouldShowFps= */
                true,
                frameStartMs)
                .addOnSuccessListener(executor) { results: T -> processLatestImage(graphicOverlay) }
    }

    // -----------------Code for processing live preview frame from CameraX API-----------------------
    @RequiresApi(VERSION_CODES.O)
    @ExperimentalGetImage
    override fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay) {
        val frameStartMs = SystemClock.elapsedRealtime()
        if (isShutdown) {
            image.close()
            return
        }
        var bitmap: Bitmap? = null
        if (!PreferenceUtils.isCameraLiveViewportEnabled(graphicOverlay.context)) {
            bitmap = BitmapUtils.getBitmap(image)
        }
        if (isMlImageEnabled(graphicOverlay.context)) {
            val mlImage = MediaMlImageBuilder(image.image!!)
                    .setRotation(image.imageInfo.rotationDegrees)
                    .build()
            requestDetectInImage(
                    mlImage,
                    graphicOverlay,  /* originalCameraImage= */
                    bitmap,  /* shouldShowFps= */
                    true,
                    frameStartMs) // When the image is from CameraX analysis use case, must call image.close() on received
                    // images when finished using them. Otherwise, new images may not be received or the
                    // camera may stall.
                    // Currently MlImage doesn't support ImageProxy directly, so we still need to call
                    // ImageProxy.close() here.
                    .addOnCompleteListener { results: Task<T>? -> image.close() }
            return
        }
        requestDetectInImage(
                InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees),
                graphicOverlay,  /* originalCameraImage= */
                bitmap,  /* shouldShowFps= */
                true,
                frameStartMs) // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                .addOnCompleteListener { results: Task<T>? -> image.close() }
    }

    // -----------------Common processing logic-------------------------------------------------------
    private fun requestDetectInImage(
            image: InputImage,
            graphicOverlay: GraphicOverlay,
            originalCameraImage: Bitmap?,
            shouldShowFps: Boolean,
            frameStartMs: Long): Task<T> {
        return setUpListener(
                detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs)
    }

    private fun requestDetectInImage(
            image: MlImage,
            graphicOverlay: GraphicOverlay,
            originalCameraImage: Bitmap?,
            shouldShowFps: Boolean,
            frameStartMs: Long): Task<T> {
        return setUpListener(
                detectInImage(image), graphicOverlay, originalCameraImage, shouldShowFps, frameStartMs)
    }

    private fun setUpListener(
            task: Task<T>,
            graphicOverlay: GraphicOverlay,
            originalCameraImage: Bitmap?,
            shouldShowFps: Boolean,
            frameStartMs: Long): Task<T> {
        val detectorStartMs = SystemClock.elapsedRealtime()
        return task.addOnSuccessListener(
                executor
        ) { results: T ->
            val endMs = SystemClock.elapsedRealtime()
            val currentFrameLatencyMs = endMs - frameStartMs
            val currentDetectorLatencyMs = endMs - detectorStartMs
            if (numRuns >= 500) {
                resetLatencyStats()
            }
            numRuns++
            frameProcessedInOneSecondInterval++
            totalFrameMs += currentFrameLatencyMs
            maxFrameMs = Math.max(currentFrameLatencyMs, maxFrameMs)
            minFrameMs = Math.min(currentFrameLatencyMs, minFrameMs)
            totalDetectorMs += currentDetectorLatencyMs
            maxDetectorMs = Math.max(currentDetectorLatencyMs, maxDetectorMs)
            minDetectorMs = Math.min(currentDetectorLatencyMs, minDetectorMs)

            // Only log inference info once per second. When frameProcessedInOneSecondInterval is
            // equal to 1, it means this is the first frame processed during the current second.
            if (frameProcessedInOneSecondInterval == 1) {
                Log.d(TAG, "Num of Runs: $numRuns")
                Log.d(
                        TAG,
                        "Frame latency: max="
                                + maxFrameMs
                                + ", min="
                                + minFrameMs
                                + ", avg="
                                + totalFrameMs / numRuns)
                Log.d(
                        TAG,
                        "Detector latency: max="
                                + maxDetectorMs
                                + ", min="
                                + minDetectorMs
                                + ", avg="
                                + totalDetectorMs / numRuns)
                val mi = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(mi)
                val availableMegs = mi.availMem / 0x100000L
                Log.d(TAG, "Memory available in system: $availableMegs MB")
                temperatureMonitor.logTemperature()
            }
            graphicOverlay.clear()
            if (originalCameraImage != null) {
                graphicOverlay.add(CameraImageGraphic(graphicOverlay, originalCameraImage))
            }
            this@VisionProcessorBase.onSuccess(results, graphicOverlay)
            if (!PreferenceUtils.shouldHideDetectionInfo(graphicOverlay.context)) {
                graphicOverlay.add(
                        InferenceInfoGraphic(
                                graphicOverlay,
                                currentFrameLatencyMs,
                                currentDetectorLatencyMs,
                                if (shouldShowFps) framesPerSecond else null))
            }
            graphicOverlay.postInvalidate()
        }
                .addOnFailureListener(
                        executor
                ) { e: Exception ->
                    graphicOverlay.clear()
                    graphicOverlay.postInvalidate()
                    val error = "Failed to process. Error: " + e.localizedMessage
                    Toast.makeText(
                            graphicOverlay.context,
                            """
                    $error
                    Cause: ${e.cause}
                    """.trimIndent(),
                            Toast.LENGTH_SHORT)
                            .show()
                    Log.d(TAG, error)
                    e.printStackTrace()
                    this@VisionProcessorBase.onFailure(e)
                }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
        resetLatencyStats()
        fpsTimer.cancel()
        temperatureMonitor.stop()
    }

    private fun resetLatencyStats() {
        numRuns = 0
        totalFrameMs = 0
        maxFrameMs = 0
        minFrameMs = Long.MAX_VALUE
        totalDetectorMs = 0
        maxDetectorMs = 0
        minDetectorMs = Long.MAX_VALUE
    }

    protected abstract fun detectInImage(image: InputImage?): Task<T>
    protected open fun detectInImage(image: MlImage?): Task<T> {
        return Tasks.forException(
                MlKitException(
                        "MlImage is currently not demonstrated for this feature",
                        MlKitException.INVALID_ARGUMENT))
    }

    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)
    protected abstract fun onFailure(e: Exception)
    protected open fun isMlImageEnabled(context: Context?): Boolean {
        return false
    }

    companion object {
        protected const val MANUAL_TESTING_LOG = "LogTagForTest"
        private const val TAG = "VisionProcessorBase"
    }
}