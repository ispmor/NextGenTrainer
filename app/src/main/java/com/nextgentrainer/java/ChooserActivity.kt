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
import android.os.Looper
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
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageAnalysis
import com.nextgentrainer.java.CameraActivity
import androidx.camera.core.CameraSelector
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import android.content.Intent
import com.nextgentrainer.preference.SettingsActivity
import com.nextgentrainer.preference.SettingsActivity.LaunchSource
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.MlKitException
import android.app.Activity
import android.os.Build
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import android.widget.AdapterView.OnItemClickListener
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import com.nextgentrainer.java.ChooserActivity
import android.app.ActivityManager
import com.google.android.gms.tasks.TaskExecutors
import android.graphics.Bitmap
import com.google.android.odml.image.BitmapMlImageBuilder
import com.google.android.odml.image.ByteBufferMlImageBuilder
import com.google.android.gms.tasks.OnSuccessListener
import androidx.camera.core.ExperimentalGetImage
import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks
import androidx.annotation.StringRes
import android.preference.PreferenceManager
import com.nextgentrainer.CameraSource.SizePair
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
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.*
import android.widget.*
import com.nextgentrainer.*

@RequiresApi(api = VERSION_CODES.O)
class ChooserActivity : AppCompatActivity(), OnItemClickListener, View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    ThreadPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setVmPolicy(
                    VmPolicy.Builder()
                            .detectLeakedSqlLiteObjects()
                            .detectLeakedClosableObjects()
                            .penaltyLog()
                            .build())
        }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_chooser_training)
        val repCounter = findViewById<Button>(R.id.rep_counter_button)
        repCounter.setOnClickListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val clicked = CLASSES[position]
        startActivity(Intent(this, clicked))
    }

    override fun onClick(v: View) {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    companion object {
        private const val TAG = "ChooserActivity"
        private val CLASSES = arrayOf<Class<*>>(
                CameraActivity::class.java
        )
        private val DESCRIPTION_IDS = intArrayOf(
                R.string.desc_pushups //desc_camerax_live_preview_activity
        )
    }
}