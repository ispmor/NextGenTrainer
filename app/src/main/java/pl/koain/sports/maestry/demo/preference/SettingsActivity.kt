package pl.koain.sports.maestry.demo.preference

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
import android.widget.CompoundButton
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageAnalysis
import pl.koain.sports.maestry.demo.VisionImageProcessor
import pl.koain.sports.maestry.demo.java.CameraActivity
import androidx.camera.core.CameraSelector
import android.os.Bundle
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ToggleButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import pl.koain.sports.maestry.demo.CameraXViewModel
import android.content.Intent
import pl.koain.sports.maestry.demo.preference.SettingsActivity
import pl.koain.sports.maestry.demo.preference.SettingsActivity.LaunchSource
import android.widget.AdapterView
import androidx.camera.core.CameraInfoUnavailableException
import android.widget.Toast
import androidx.camera.core.ImageProxy
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
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.components.XAxis
import android.widget.AdapterView.OnItemClickListener
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import pl.koain.sports.maestry.demo.java.ChooserActivity
import android.view.WindowManager
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
import androidx.camera.core.ExperimentalGetImage
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
import android.view.SurfaceHolder
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
import java.lang.Exception
import java.lang.RuntimeException

/**
 * Hosts the preference fragment to configure settings for a demo activity that specified by the
 * [LaunchSource].
 */
class SettingsActivity : AppCompatActivity() {
    /**
     * Specifies where this activity is launched from.
     */
    // CameraX is only available on API 21+
    enum class LaunchSource(val titleResId: Int, val prefFragmentClass: Class<out PreferenceFragment>) {
        CAMERAX_LIVE_PREVIEW(
                R.string.pref_screen_title_camerax_live_preview,
                CameraXLivePreviewPreferenceFragment::class.java);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val launchSource = intent.getSerializableExtra(EXTRA_LAUNCH_SOURCE) as LaunchSource?
        val actionBar = supportActionBar
        actionBar?.setTitle(launchSource!!.titleResId)
        try {
            fragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.settings_container,
                            launchSource!!.prefFragmentClass.getDeclaredConstructor().newInstance())
                    .commit()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        const val EXTRA_LAUNCH_SOURCE = "extra_launch_source"
    }
}