package co.nextgentrainer.kotlin.utils

import android.content.Context
import android.net.Uri
import co.nextgentrainer.R
import co.nextgentrainer.kotlin.data.repository.MovementRepository
import co.nextgentrainer.kotlin.data.repository.RepetitionRepository
import co.nextgentrainer.kotlin.data.repository.WorkoutRepository
import co.nextgentrainer.kotlin.posedetector.ExerciseProcessor
import co.nextgentrainer.kotlin.utils.Constants.RECORD
import co.nextgentrainer.kotlin.utils.Constants.SQUATS_TRAINER
import co.nextgentrainer.preference.PreferenceUtils
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

object CameraActivityHelper {
    fun selectModel(
        selectedModel: String,
        context: Context,
        movementRepository: MovementRepository,
        repetitionRepository: RepetitionRepository,
        workoutRepository: WorkoutRepository
    ): ExerciseProcessor {
        return when (selectedModel) {
//            REP_COUNTER -> {
//                val poseDetectorOptions =
//                    PreferenceUtils.getPoseDetectorOptionsForLivePreview(context)
//
//                ExerciseProcessor(
//                    context,
//                    poseDetectorOptions,
//                    true, /* isStreamMode = */
//                    true,
//                    "all",
//                    movementRepository,
//                    repetitionRepository,
//                    workoutRepository,
//                )
//            }
//            PUSH_UPS_TRAINER -> ExerciseProcessor(
//                context,
//                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
//                true,
//                true,
//                "pushups",
//                movementRepository,
//                repetitionRepository,
//                workoutRepository
//            )
//            PULL_UPS_TRAINER -> ExerciseProcessor(
//                context,
//                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
//                true,
//                true,
//                "pullups",
//                movementRepository,
//                repetitionRepository,
//                workoutRepository
//            )
//            SIT_UPS_TRAINER -> ExerciseProcessor(
//                context,
//                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
//                true,
//                true,
//                "situps",
//                movementRepository,
//                repetitionRepository,
//                workoutRepository
//            )
            SQUATS_TRAINER -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                true,
                true,
                "squats",
                movementRepository,
                repetitionRepository,
                workoutRepository
            )
            RECORD -> ExerciseProcessor(
                context,
                PreferenceUtils.getPoseDetectorOptionsForLivePreview(context),
                false,
                true,
                "recording",
                movementRepository,
                repetitionRepository,
                workoutRepository
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
