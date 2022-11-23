package com.nextgentrainer.kotlin.utils

import android.content.Context
import android.net.Uri
import com.nextgentrainer.R
import com.nextgentrainer.kotlin.data.repository.MovementRepository
import com.nextgentrainer.kotlin.data.repository.RepetitionRepository
import com.nextgentrainer.kotlin.posedetector.ExerciseProcessor
import com.nextgentrainer.kotlin.utils.Constants.PULL_UPS_TRAINER
import com.nextgentrainer.kotlin.utils.Constants.PUSH_UPS_TRAINER
import com.nextgentrainer.kotlin.utils.Constants.REP_COUNTER
import com.nextgentrainer.kotlin.utils.Constants.SIT_UPS_TRAINER
import com.nextgentrainer.kotlin.utils.Constants.SQUATS_TRAINER
import com.nextgentrainer.preference.PreferenceUtils
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

object CameraActivityHelper {
    fun selectModel(
        selectedModel: String,
        context: Context,
        movementRepository: MovementRepository,
        repetitionRepository: RepetitionRepository
    ): ExerciseProcessor {
        return when (selectedModel) {
            REP_COUNTER -> {
                val poseDetectorOptions =
                    PreferenceUtils.getPoseDetectorOptionsForLivePreview(context)

                ExerciseProcessor(
                    context,
                    poseDetectorOptions,
                    true, /* isStreamMode = */
                    true,
                    "all",
                    movementRepository,
                    repetitionRepository
                )
            }
            PUSH_UPS_TRAINER -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                true,
                true,
                "pushups",
                movementRepository,
                repetitionRepository
            )
            PULL_UPS_TRAINER -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                true,
                true,
                "pullups",
                movementRepository,
                repetitionRepository
            )
            SIT_UPS_TRAINER -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                true,
                true,
                "situps",
                movementRepository,
                repetitionRepository
            )
            SQUATS_TRAINER -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                true,
                true,
                "squats",
                movementRepository,
                repetitionRepository
            )
            else -> throw IllegalStateException("Invalid model name")
        }
    }

    fun saveDataToFileInExternalStorage(data: String?, uri: Uri?, context: Context) {
        context.contentResolver.openFileDescriptor(uri!!, "w").use { csv ->
            FileOutputStream(csv!!.fileDescriptor).use { fileOutputStream ->
                fileOutputStream.write(
                    data!!.toByteArray(
                        StandardCharsets.UTF_8
                    )
                )
            }
        }
    }

    fun saveDataToCache(data: String?, uri: String = "", context: Context) {
        val finalCacheFileName = if (uri == "") context.getString(R.string.cache_filename) else uri
        context.openFileOutput(finalCacheFileName, Context.MODE_APPEND)
            .use { fos -> fos.write(data!!.toByteArray(StandardCharsets.UTF_8)) }
    }
}
