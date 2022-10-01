package pl.koain.sports.maestry.demo.java

import pl.koain.sports.maestry.demo.java.posedetector.classification.RepetitionCounter
import pl.koain.sports.maestry.demo.java.utils.RepetitionQuality
import pl.koain.sports.maestry.demo.java.utils.Repetition
import com.google.mlkit.vision.common.PointF3D
import pl.koain.sports.maestry.demo.java.utils.LineEquation
import com.google.mlkit.vision.pose.Pose
import pl.koain.sports.maestry.demo.java.utils.QualityFeature
import pl.koain.sports.maestry.demo.java.posedetector.MovementDescription
import pl.koain.sports.maestry.demo.java.utils.QualityDetector
import pl.koain.sports.maestry.demo.GraphicOverlay
import pl.koain.sports.maestry.demo.GraphicOverlay.Graphic
import pl.koain.sports.maestry.demo.java.graphics.QualityGraphics
import com.google.common.primitives.Floats
import pl.koain.sports.maestry.demo.java.posedetector.classification.PoseEmbedding
import pl.koain.sports.maestry.demo.java.posedetector.classification.PoseSample
import kotlin.jvm.JvmOverloads
import pl.koain.sports.maestry.demo.java.posedetector.classification.EMASmoothing
import pl.koain.sports.maestry.demo.java.posedetector.classification.ClassificationResult
import com.google.mlkit.vision.pose.PoseLandmark
import pl.koain.sports.maestry.demo.java.posedetector.classification.PoseClassifier
import android.media.MediaPlayer
import pl.koain.sports.maestry.demo.java.utils.ExerciseSet
import android.os.Looper
import pl.koain.sports.maestry.demo.R
import pl.koain.sports.maestry.demo.java.posedetector.classification.PoseClassifierProcessor
import com.google.gson.Gson
import pl.koain.sports.maestry.demo.java.posedetector.PoseGraphic
import com.google.common.primitives.Ints
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import pl.koain.sports.maestry.demo.java.VisionProcessorBase
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.common.InputImage
import com.google.android.odml.image.MlImage
import pl.koain.sports.maestry.demo.java.graphics.CustomPoseGraphics
import pl.koain.sports.maestry.demo.java.posedetector.ExerciseProcessor
import pl.koain.sports.maestry.demo.java.posedetector.PoseDetectorProcessor
import com.google.android.gms.common.annotation.KeepName
import androidx.annotation.RequiresApi
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView.OnItemSelectedListener
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import pl.koain.sports.maestry.demo.VisionImageProcessor
import pl.koain.sports.maestry.demo.java.CameraActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import pl.koain.sports.maestry.demo.CameraXViewModel
import android.content.Intent
import pl.koain.sports.maestry.demo.preference.SettingsActivity
import pl.koain.sports.maestry.demo.preference.SettingsActivity.LaunchSource
import com.google.mlkit.common.MlKitException
import android.app.Activity
import android.os.Build
import pl.koain.sports.maestry.demo.java.FitLogActivity
import pl.koain.sports.maestry.demo.java.FitLogActivity.MyArrayAdapter
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import android.widget.AdapterView.OnItemClickListener
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import pl.koain.sports.maestry.demo.java.ChooserActivity
import android.app.ActivityManager
import pl.koain.sports.maestry.demo.ScopedExecutor
import pl.koain.sports.maestry.demo.TemperatureMonitor
import pl.koain.sports.maestry.demo.FrameMetadata
import com.google.android.gms.tasks.TaskExecutors
import android.graphics.Bitmap
import com.google.android.odml.image.BitmapMlImageBuilder
import pl.koain.sports.maestry.demo.BitmapUtils
import com.google.android.odml.image.ByteBufferMlImageBuilder
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.gms.tasks.OnCompleteListener
import pl.koain.sports.maestry.demo.CameraImageGraphic
import pl.koain.sports.maestry.demo.InferenceInfoGraphic
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks
import androidx.annotation.StringRes
import android.preference.PreferenceManager
import pl.koain.sports.maestry.demo.CameraSource.SizePair
import pl.koain.sports.maestry.demo.CameraSource
import android.content.SharedPreferences
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import android.preference.PreferenceFragment
import pl.koain.sports.maestry.demo.preference.CameraXLivePreviewPreferenceFragment
import android.preference.PreferenceCategory
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import pl.koain.sports.maestry.demo.preference.LivePreviewPreferenceFragment
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
import pl.koain.sports.maestry.demo.CameraSource.FrameProcessingRunnable
import androidx.annotation.RequiresPermission
import android.annotation.SuppressLint
import pl.koain.sports.maestry.demo.CameraSource.CameraPreviewCallback
import android.hardware.Camera.PreviewCallback
import android.view.View.OnLayoutChangeListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.*
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import pl.koain.sports.maestry.demo.preference.PreferenceUtils
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

/**
 * Live preview demo app for ML Kit APIs using CameraX.
 */
@KeepName
@RequiresApi(VERSION_CODES.O)
class CameraActivity : AppCompatActivity(), OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = PUSH_UPS_TRAINER
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private var countersAsString: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, REP_COUNTER)
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        setContentView(R.layout.activity_camera_preview)
        previewView = findViewById(R.id.preview_view)
        if (previewView == null) {
            Log.d(TAG, "previewView is null")
        }
        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }
        val spinner = findViewById<Spinner>(R.id.spinner)
        val options: MutableList<String> = ArrayList()
        options.add(REP_COUNTER)
        options.add(PUSH_UPS_TRAINER)
        options.add(PULL_UPS_TRAINER)
        options.add(SQUATS_TRAINER)
        options.add(SIT_UPS_TRAINER)


        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, R.layout.spinner_style, options)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = dataAdapter
        spinner.onItemSelectedListener = this
        val facingSwitch = findViewById<ToggleButton>(R.id.facing_switch)
        facingSwitch.setOnCheckedChangeListener(this)
        ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))
                .get(CameraXViewModel::class.java)
                .processCameraProvider
                .observe(
                        this
                ) { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    bindAllCameraUseCases()
                }
        val settingsButton = findViewById<ImageView>(R.id.settings_button)
        settingsButton.setOnClickListener { v: View? ->
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            intent.putExtra(
                    SettingsActivity.Companion.EXTRA_LAUNCH_SOURCE,
                    LaunchSource.CAMERAX_LIVE_PREVIEW)
            startActivity(intent)
        }
        val saveButton = findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            assert(imageProcessor != null)
            val counters = (imageProcessor as ExerciseProcessor?)!!.repetitionCounters
            countersAsString = counters!!.stream().map { obj: RepetitionCounter? -> obj.toString() }.collect(Collectors.joining("\n"))
            saveDataToCache(countersAsString)
            createCSVDocumentPicker()
        }
    }

    private fun createCSVDocumentPicker() {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val today = formatter.format(date)
        val fileName = "$today.csv"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putString(STATE_SELECTED_MODEL, selectedModel)
    }

    @Synchronized
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedModel = parent.getItemAtPosition(pos).toString()
        Log.d(TAG, "Selected model: $selectedModel")
        bindAnalysisUseCase()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing.
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (cameraProvider == null) {
            return
        }
        val newLensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Log.d(TAG, "Set facing to $newLensFacing")
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            // Falls through
        }
        Toast.makeText(
                applicationContext,
                "This device does not have lens with facing: $newLensFacing",
                Toast.LENGTH_SHORT)
                .show()
    }

    public override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return
        }
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle( /* lifecycleOwner= */this, cameraSelector!!, previewUseCase)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor = try {
            when (selectedModel) {
                REP_COUNTER -> {
                    val poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                    val shouldShowInFrameLikelihood = PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this)
                    val visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this)
                    val rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this)
                    val runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this)
                    ExerciseProcessor(
                            this,
                            poseDetectorOptions,
                            true,  /* isStreamMode = */
                            true,
                            "all")
                }
                PUSH_UPS_TRAINER -> ExerciseProcessor(
                        this,
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(this),
                        true,
                        true,
                        "pushups")
                PULL_UPS_TRAINER -> ExerciseProcessor(
                        this,
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(this),
                        true,
                        true,
                        "pullups")
                SIT_UPS_TRAINER -> ExerciseProcessor(
                        this,
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(this),
                        true,
                        true,
                        "situps")
                SQUATS_TRAINER -> ExerciseProcessor(
                        this,
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(this),
                        true,
                        true,
                        "squats")
                else -> throw IllegalStateException("Invalid model name")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $selectedModel", e)
            Toast.makeText(
                    applicationContext,
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG)
                    .show()
            return
        }
        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()
        needUpdateGraphicOverlayImageSourceInfo = true
        analysisUseCase!!.setAnalyzer( // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this)
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicOverlay!!.setImageSourceInfo(
                            imageProxy.width, imageProxy.height, isImageFlipped)
                } else {
                    graphicOverlay!!.setImageSourceInfo(
                            imageProxy.height, imageProxy.width, isImageFlipped)
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                imageProcessor!!.processImageProxy(imageProxy, graphicOverlay!!)
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
            }
        }
        cameraProvider!!.bindToLifecycle( /* lifecycleOwner= */this, cameraSelector!!, analysisUseCase)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            val uri: Uri?
            if (data != null) {
                uri = data.data
                saveDataToFileInExternalStorage(countersAsString, uri)
            }
        }
    }

    @JvmOverloads
    fun saveDataToCache(data: String?, uri: String = "") {
        val finalCacheFileName = if (uri == "") getString(R.string.cache_filename) else uri
        try {
            openFileOutput(finalCacheFileName, MODE_APPEND).use { fos -> fos.write(data!!.toByteArray(StandardCharsets.UTF_8)) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveDataToFileInExternalStorage(data: String?, uri: Uri?) {
        try {
            contentResolver.openFileDescriptor(uri!!, "w").use { csv -> FileOutputStream(csv!!.fileDescriptor).use { fileOutputStream -> fileOutputStream.write(data!!.toByteArray(StandardCharsets.UTF_8)) } }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val CREATE_FILE = 1
        private const val TAG = "CameraActivity"
        private const val REP_COUNTER = "Repetition Counter"
        private const val PUSH_UPS_TRAINER = "Push-ups Trainer"
        private const val SIT_UPS_TRAINER = "Sit-ups Trainer"
        private const val SQUATS_TRAINER = "Squats Trainer"
        private const val PULL_UPS_TRAINER = "Pull-ups Trainer"
        private const val STATE_SELECTED_MODEL = "selected_model"
    }
}