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
import android.content.Context
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
import android.util.Size
import com.google.common.base.Preconditions
import java.lang.Exception

/**
 * Utility class to retrieve shared preferences.
 */
object PreferenceUtils {
    private const val POSE_DETECTOR_PERFORMANCE_MODE_FAST = 1
    fun saveString(context: Context, @StringRes prefKeyId: Int, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(prefKeyId), value)
                .apply()
    }

    fun getCameraPreviewSizePair(context: Context, cameraId: Int): SizePair? {
        Preconditions.checkArgument(cameraId == CameraSource.Companion.CAMERA_FACING_BACK
                || cameraId == CameraSource.Companion.CAMERA_FACING_FRONT)
        val previewSizePrefKey: String
        val pictureSizePrefKey: String
        if (cameraId == CameraSource.Companion.CAMERA_FACING_BACK) {
            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size)
            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size)
        } else {
            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size)
            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size)
        }
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            SizePair(
                    com.google.android.gms.common.images.Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)!!),
                    com.google.android.gms.common.images.Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)!!))
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(VERSION_CODES.O)
    fun getCameraXTargetResolution(context: Context, lensfacing: Int): Size? {
        Preconditions.checkArgument(lensfacing == CameraSelector.LENS_FACING_BACK
                || lensfacing == CameraSelector.LENS_FACING_FRONT)
        val prefKey = if (lensfacing == CameraSelector.LENS_FACING_BACK) context.getString(R.string.pref_key_camerax_rear_camera_target_resolution) else context.getString(R.string.pref_key_camerax_front_camera_target_resolution)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            Size.parseSize(sharedPreferences.getString(prefKey, null))
        } catch (e: Exception) {
            null
        }
    }

    fun shouldHideDetectionInfo(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_info_hide)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    fun getObjectDetectorOptionsForStillImage(context: Context): ObjectDetectorOptions {
        return getObjectDetectorOptions(
                context,
                R.string.pref_key_still_image_object_detector_enable_multiple_objects,
                R.string.pref_key_still_image_object_detector_enable_classification,
                ObjectDetectorOptions.SINGLE_IMAGE_MODE)
    }

    fun getObjectDetectorOptionsForLivePreview(context: Context): ObjectDetectorOptions {
        return getObjectDetectorOptions(
                context,
                R.string.pref_key_live_preview_object_detector_enable_multiple_objects,
                R.string.pref_key_live_preview_object_detector_enable_classification,
                ObjectDetectorOptions.STREAM_MODE)
    }

    private fun getObjectDetectorOptions(
            context: Context,
            @StringRes prefKeyForMultipleObjects: Int,
            @StringRes prefKeyForClassification: Int,
            @ObjectDetectorOptionsBase.DetectorMode mode: Int): ObjectDetectorOptions {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val enableMultipleObjects = sharedPreferences.getBoolean(context.getString(prefKeyForMultipleObjects), false)
        val enableClassification = sharedPreferences.getBoolean(context.getString(prefKeyForClassification), true)
        val builder = ObjectDetectorOptions.Builder().setDetectorMode(mode)
        if (enableMultipleObjects) {
            builder.enableMultipleObjects()
        }
        if (enableClassification) {
            builder.enableClassification()
        }
        return builder.build()
    }

    fun getCustomObjectDetectorOptionsForStillImage(
            context: Context, localModel: LocalModel): CustomObjectDetectorOptions {
        return getCustomObjectDetectorOptions(
                context,
                localModel,
                R.string.pref_key_still_image_object_detector_enable_multiple_objects,
                R.string.pref_key_still_image_object_detector_enable_classification,
                CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
    }

    fun getCustomObjectDetectorOptionsForLivePreview(
            context: Context, localModel: LocalModel): CustomObjectDetectorOptions {
        return getCustomObjectDetectorOptions(
                context,
                localModel,
                R.string.pref_key_live_preview_object_detector_enable_multiple_objects,
                R.string.pref_key_live_preview_object_detector_enable_classification,
                CustomObjectDetectorOptions.STREAM_MODE)
    }

    private fun getCustomObjectDetectorOptions(
            context: Context,
            localModel: LocalModel,
            @StringRes prefKeyForMultipleObjects: Int,
            @StringRes prefKeyForClassification: Int,
            @ObjectDetectorOptionsBase.DetectorMode mode: Int): CustomObjectDetectorOptions {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val enableMultipleObjects = sharedPreferences.getBoolean(context.getString(prefKeyForMultipleObjects), false)
        val enableClassification = sharedPreferences.getBoolean(context.getString(prefKeyForClassification), true)
        val builder = CustomObjectDetectorOptions.Builder(localModel).setDetectorMode(mode)
        if (enableMultipleObjects) {
            builder.enableMultipleObjects()
        }
        if (enableClassification) {
            builder.enableClassification().setMaxPerObjectLabelCount(1)
        }
        return builder.build()
    }

    fun getFaceDetectorOptions(context: Context): FaceDetectorOptions {
        val landmarkMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_live_preview_face_detection_landmark_mode,
                FaceDetectorOptions.LANDMARK_MODE_NONE)
        val contourMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_live_preview_face_detection_contour_mode,
                FaceDetectorOptions.CONTOUR_MODE_ALL)
        val classificationMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_live_preview_face_detection_classification_mode,
                FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        val performanceMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_live_preview_face_detection_performance_mode,
                FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val enableFaceTracking = sharedPreferences.getBoolean(
                context.getString(R.string.pref_key_live_preview_face_detection_face_tracking), false)
        val minFaceSize =
                sharedPreferences.getString(
                        context.getString(R.string.pref_key_live_preview_face_detection_min_face_size),
                        "0.1")!!.toFloat()
        val optionsBuilder = FaceDetectorOptions.Builder()
                .setLandmarkMode(landmarkMode)
                .setContourMode(contourMode)
                .setClassificationMode(classificationMode)
                .setPerformanceMode(performanceMode)
                .setMinFaceSize(minFaceSize)
        if (enableFaceTracking) {
            optionsBuilder.enableTracking()
        }
        return optionsBuilder.build()
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_live_preview_pose_detection_performance_mode,
                POSE_DETECTOR_PERFORMANCE_MODE_FAST)
        val preferGPU = preferGPUForPoseDetection(context)
        return if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            val builder = PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        } else {
            val builder = AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        }
    }

    fun getPoseDetectorOptionsForStillImage(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
                context,
                R.string.pref_key_still_image_pose_detection_performance_mode,
                POSE_DETECTOR_PERFORMANCE_MODE_FAST)
        val preferGPU = preferGPUForPoseDetection(context)
        return if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            val builder = PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        } else {
            val builder = AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
            if (preferGPU) {
                builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
            }
            builder.build()
        }
    }

    fun shouldGroupRecognizedTextInBlocks(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_group_recognized_text_in_blocks)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    fun showLanguageTag(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_show_language_tag)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    fun preferGPUForPoseDetection(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_prefer_gpu)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldShowPoseDetectionInFrameLikelihoodLivePreview(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_live_preview_pose_detector_show_in_frame_likelihood)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldShowPoseDetectionInFrameLikelihoodStillImage(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_still_image_pose_detector_show_in_frame_likelihood)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldPoseDetectionVisualizeZ(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_visualize_z)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldPoseDetectionRescaleZForVisualization(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_rescale_z)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldPoseDetectionRunClassification(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_run_classification)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    fun shouldSegmentationEnableRawSizeMask(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_segmentation_raw_size_mask)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    /**
     * Mode type preference is backed by [android.preference.ListPreference] which only support
     * storing its entry value as string type, so we need to retrieve as string and then convert to
     * integer.
     */
    private fun getModeTypePreferenceValue(
            context: Context, @StringRes prefKeyResId: Int, defaultValue: Int): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(prefKeyResId)
        return sharedPreferences.getString(prefKey, defaultValue.toString())!!.toInt()
    }

    fun isCameraLiveViewportEnabled(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_camera_live_viewport)
        return sharedPreferences.getBoolean(prefKey, false)
    }
}