package com.nextgentrainer.kotlin.posedetector.classification

import android.content.Context
import android.media.MediaPlayer
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.common.base.Preconditions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.mlkit.vision.pose.Pose
import com.nextgentrainer.R
import com.nextgentrainer.kotlin.data.model.ExerciseSetOld
import com.nextgentrainer.kotlin.data.model.Repetition
import com.nextgentrainer.kotlin.data.model.RepetitionQuality
import com.nextgentrainer.kotlin.data.repository.MovementRepository
import com.nextgentrainer.kotlin.data.repository.RepetitionRepository
import com.nextgentrainer.kotlin.data.repository.WorkoutRepository
import com.nextgentrainer.kotlin.utils.QualityDetector
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Date
import java.util.Locale
import kotlin.collections.set

/**
 * Accepts a stream of [Pose] for classification and Rep counting.
 */

class PoseClassifierProcessor @WorkerThread constructor(
    context: Context,
    isStreamMode: Boolean,
    var baseExercise: String,
    movementRepository: MovementRepository,
    private val repetitionRepository: RepetitionRepository,
    val workoutRepository: WorkoutRepository
) {
    private val isStreamMode: Boolean
    private val lastDetectedClasses: MutableList<String> = mutableListOf()
    private var lastDetectedClass: String? = ""
    private var emaSmoothing: EMASmoothing? = null
    private var repCounters: MutableMap<String?, RepetitionCounter?>? = null
    private var poseClassifier: PoseClassifier? = null
    private var lastRepResult: String? = null
    private val context: Context
    private var lastRep: Repetition
    private val sets: MutableMap<String, List<ExerciseSetOld>> = HashMap()
    private var posesFromLastRep: MutableList<Pose> = ArrayList()
    private var posesTimestampsFromLastRep: MutableList<Date> = ArrayList()
    private val qualityDetector = QualityDetector(movementRepository)
    private val user: FirebaseUser
    private val lastClassification: MutableList<ClassificationResult> = mutableListOf()
    private var recording = false

    init {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        this.isStreamMode = isStreamMode
        if (isStreamMode) {
            emaSmoothing = EMASmoothing()
            repCounters = HashMap()
            lastRepResult = ""
        }
        this.context = context
        if (baseExercise == "") {
            this.baseExercise = context.getString(R.string.all_exercises)
        }
        lastRep = repetitionRepository.getEmptyRepetition()

        if (baseExercise != "recording") {
            loadPoseSamples(context, baseExercise)
        } else {
            poseClassifier = PoseClassifier(listOf())
            recording = true
        }

        user = Firebase.auth.currentUser!!
    }

    private fun loadPoseSamples(context: Context, baseExercise: String) {
        val poseSamples: MutableList<PoseSample> = ArrayList()
        BufferedReader(
            InputStreamReader(
                context.assets.open("$BASE_DIR/$baseExercise/$SAMPLES_FILE")
            )
        )
            .use { reader ->
                reader.lines().forEach { line ->
                    val poseSample: PoseSample? = PoseSample.getPoseSample(line, ",")
                    poseSample?.let { poseSamples.add(it) }
                }
            }

        poseClassifier = PoseClassifier(poseSamples)
        if (isStreamMode) {
            for (className in POSE_CLASSES) {
                repCounters!![className] = RepetitionCounter(className)
                sets[className] = ArrayList()
            }
        }
    }

    /**
     * Given a new [Pose] input, returns a list of formatted [String]s with Pose
     * classification results.
     *
     *
     * Currently it returns up to 2 strings as following:
     * 0: PoseClass : X reps
     * 1: PoseClass : [0.0-1.0] confidence
     *
     * @return
     */
    @WorkerThread
    fun getPoseResult(pose: Pose): Repetition {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String?> = ArrayList()
        var classification: ClassificationResult
        if (recording) {
            classification = ClassificationResult()
            classification.classConfidences["recording"] = 1.0f
        } else {
            classification = poseClassifier!!.classify(pose)
        }

        // Update {@link RepetitionCounter}s if {@code isStreamMode}.
        if (isStreamMode) {
            // Feed pose to smoothing even if no pose found.
            classification = emaSmoothing!!.getSmoothedResult(classification)

            // Return early without updating repCounter if no pose found.
            if (pose.allPoseLandmarks.isEmpty()) {
                result.add(lastRepResult)
                return lastRep
            }
            posesFromLastRep.add(pose)
            posesTimestampsFromLastRep.add(Date())
            val actualRepetition = checkRepetitions(classification)
            lastRep = actualRepetition
        }

        // Add maxConfidence class of current frame to result if pose is found.
        if (pose.allPoseLandmarks.isNotEmpty()) {
            val maxConfidenceClass = classification.maxConfidenceClass
            val maxConfidenceClassResult = String.format(
                Locale.US,
                "%s : %.2f confidence",
                maxConfidenceClass,
                classification.getClassConfidence(maxConfidenceClass) / poseClassifier!!.confidenceRange()
            )
//            lastRep!!.poseName = maxConfidenceClass
//            lastRep!!.confidence = classification.getClassConfidence(maxConfidenceClass)
            result.add(maxConfidenceClassResult)
        }

        return lastRep
    }

    private fun checkRepetitions(classification: ClassificationResult): Repetition {
        val maxConfidenceClass = classification.maxConfidenceClass
        val detectionResultHasChanged = lastDetectedClass != maxConfidenceClass
        if (detectionResultHasChanged) {
            lastDetectedClass = maxConfidenceClass
            lastClassification.add(classification)
            lastDetectedClasses.add(maxConfidenceClass.orEmpty())
            if (maxConfidenceClass != "" && lastDetectedClasses.size == 3 &&
                repCounters!!.containsKey(lastDetectedClasses[1])
            ) {
                lastDetectedClass = maxConfidenceClass
                val successClass = lastDetectedClasses[1]
                val repCounter = repCounters!![successClass]
                val repsBefore = repCounter!!.numRepeats
                // int repsAfter = repCounter.addClassificationResult(classification);
                val repsAfter = repCounter.increaseCount(lastClassification[1])
                Log.d(TAG, "RepsBefore: $repsBefore Reps after: $repsAfter")

                val repetitionQuality = gradeQuality(posesFromLastRep, posesTimestampsFromLastRep)
                MediaPlayer.create(context, R.raw.notification).start()
                lastRepResult = String.format(
                    Locale.US,
                    "%s : %d reps",
                    repCounter.className,
                    repsAfter
                )
                lastRep = repetitionRepository.createRepetition(
                    successClass,
                    lastClassification[1].getClassConfidence(successClass),
                    repCounter,
                    repetitionQuality,
                    user.uid
                )
                Log.d(
                    TAG,
                    String.format(Locale.getDefault(), "QUALITY: %s", repetitionQuality.quality)
                )
                saveRepetitionToCache(lastRep) // .posesFromLastRep
                repetitionRepository.saveRepetition(lastRep)
                posesFromLastRep = ArrayList()
                posesTimestampsFromLastRep = ArrayList()
                repCounters!![successClass] = repCounter
                lastDetectedClasses.removeFirst()
                lastDetectedClasses.removeFirst()
                lastClassification.removeFirst()
                lastClassification.removeFirst()
                return lastRep
            }
        }

        return lastRep
    }

    private fun saveRepetitionToCache(rep: Repetition) {
        val jsonbuilder = Gson()
        val result = """
            ${jsonbuilder.toJson(rep)}
            
        """.trimIndent()
        val finalCacheFileName = context.getString(R.string.cache_filename)
        try {
            context.openFileOutput(finalCacheFileName, Context.MODE_APPEND)
                .use { fos -> fos.write(result.toByteArray(StandardCharsets.UTF_8)) }
        } catch (e: IOException) {
            Log.d(TAG, e.message!!)
        }
    }

    val repetitionCounters: Map<String?, RepetitionCounter?>?
        get() = repCounters

    private fun gradeQuality(
        posesFromLastRep: List<Pose>,
        posesTimestampsFromLastRep: List<Date>
    ): RepetitionQuality {
        return when (baseExercise) {
            "pushups" -> qualityDetector.pushupsQuality(
                posesFromLastRep,
                posesTimestampsFromLastRep
            )
            "pullups" -> qualityDetector.pullupsQuality(
                posesFromLastRep,
                posesTimestampsFromLastRep
            )
            "squats" -> qualityDetector.squatQuality(
                posesFromLastRep,
                posesTimestampsFromLastRep,
                context
            )
            "situps" -> qualityDetector.situpsQuality(
                posesFromLastRep,
                posesTimestampsFromLastRep
            )
            else -> qualityDetector.allExcerciseQuality(
                posesFromLastRep,
                posesTimestampsFromLastRep
            )
        }
    }

    fun saveRecording(exerciseName: String?) {
        val exerciseNameFinal: String = exerciseName ?: baseExercise
        val recordingQuality = gradeQuality(posesFromLastRep, posesTimestampsFromLastRep)
        lastRep = repetitionRepository.createRepetition(
            exerciseNameFinal,
            1f,
            RepetitionCounter(baseExercise),
            recordingQuality,
            user.uid
        )
        repetitionRepository.saveRepetition(lastRep)

        val setToBeSaved = repetitionRepository.getSet()
        workoutRepository.addExerciseSetToWorkout(setToBeSaved!!)
        posesFromLastRep = ArrayList()
        posesTimestampsFromLastRep = ArrayList()
        Log.d(TAG, "Saved recording")
    }

    val repetitionCountersAsList: List<RepetitionCounter?>
        get() = ArrayList(repCounters!!.values)

    companion object {
        private const val TAG = "PoseClassifierProcessor"
        private const val BASE_DIR = "exercise"
        private const val SAMPLES_FILE = "samples.csv"
        private const val PUSHUPS_CLASS = "pushups_down"
        private const val SQUATS_CLASS = "squats_down"
        private const val SITUPS_CLASS = "situps_down"
        private const val PULLUPS_CLASS = "pullups_down"
        private val POSE_CLASSES = arrayOf(
            PUSHUPS_CLASS,
            SQUATS_CLASS,
            SITUPS_CLASS,
            PULLUPS_CLASS
        )
    }
}
