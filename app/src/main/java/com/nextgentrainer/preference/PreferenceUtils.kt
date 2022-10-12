package com.nextgentrainer.preference

import android.content.Context
import android.preference.PreferenceManager
import android.util.Size
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector
import com.google.android.gms.common.images.Size.parseSize
import com.google.common.base.Preconditions
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.nextgentrainer.CameraSource
import com.nextgentrainer.CameraSource.SizePair
import com.nextgentrainer.R

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
        Preconditions.checkArgument(
            cameraId == CameraSource.Companion.CAMERA_FACING_BACK ||
                cameraId == CameraSource.Companion.CAMERA_FACING_FRONT
        )
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
                parseSize(sharedPreferences.getString(previewSizePrefKey, null)!!),
                parseSize(sharedPreferences.getString(pictureSizePrefKey, null)!!)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getCameraXTargetResolution(context: Context, lensfacing: Int): Size? {
        Preconditions.checkArgument(
            lensfacing == CameraSelector.LENS_FACING_BACK ||
                lensfacing == CameraSelector.LENS_FACING_FRONT
        )
        val prefKey = if (lensfacing == CameraSelector.LENS_FACING_BACK) {
            context.getString(R.string.pref_key_camerax_rear_camera_target_resolution)
        } else {
            context.getString(R.string.pref_key_camerax_front_camera_target_resolution)
        }
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

//    private fun getObjectDetectorOptions(
//            context: Context,
//            @StringRes prefKeyForMultipleObjects: Int,
//            @StringRes prefKeyForClassification: Int,
//            @ObjectDetectorOptionsBase.DetectorMode mode: Int): ObjectDetectorOptions {
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        val enableMultipleObjects = sharedPreferences.getBoolean(context.getString(prefKeyForMultipleObjects), false)
//        val enableClassification = sharedPreferences.getBoolean(context.getString(prefKeyForClassification), true)
//        val builder = ObjectDetectorOptions.Builder().setDetectorMode(mode)
//        if (enableMultipleObjects) {
//            builder.enableMultipleObjects()
//        }
//        if (enableClassification) {
//            builder.enableClassification()
//        }
//        return builder.build()
//    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_pose_detection_performance_mode,
            POSE_DETECTOR_PERFORMANCE_MODE_FAST
        )
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

    fun preferGPUForPoseDetection(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_pose_detector_prefer_gpu)
        return sharedPreferences.getBoolean(prefKey, true)
    }

    private fun getModeTypePreferenceValue(
        context: Context,
        @StringRes prefKeyResId: Int,
        defaultValue: Int
    ): Int {
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
