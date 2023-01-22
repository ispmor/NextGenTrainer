package co.nextgentrainer.kotlin.posedetector.classification

import android.util.Log

/**
 * Counts reps for the give class.
 */
class RepetitionCounter @JvmOverloads
constructor(
    val className: String = "",
    private val enterThreshold: Float = DEFAULT_ENTER_THRESHOLD,
    private val exitThreshold: Float = DEFAULT_EXIT_THRESHOLD
) {
    var numRepeats = 0
        private set
    private var poseEntered = false

    /**
     * Adds a new Pose classification result and updates reps for given class.
     *
     * @param classificationResult {link ClassificationResult} of class to confidence values.
     * @return number of reps.
     */
    fun addClassificationResult(classificationResult: ClassificationResult): Int {
        val poseConfidence = classificationResult.getClassConfidence(className)
        if (!poseEntered) {
            Log.d("POSE", "Pose not entered")
            poseEntered = poseConfidence > enterThreshold
            return numRepeats
        }
        if (poseConfidence < exitThreshold) {
            numRepeats++
            poseEntered = false
        }
        return numRepeats
    }

    fun increaseCount(classificationResult: ClassificationResult?): Int {
        val poseConfidence = classificationResult!!.getClassConfidence(className)
        if (poseConfidence > exitThreshold) {
            numRepeats++
        }
        return numRepeats
    }

    fun removeLastRep() {
        numRepeats--
    }

    override fun toString(): String {
        return "$className,$numRepeats"
    }

    companion object {
        // These thresholds can be tuned in conjunction with the Top K values in
        // {@link PoseClassifier}.
        // The default Top K value is 10 so the range here is [0-10].
        private const val DEFAULT_ENTER_THRESHOLD = 6f
        private const val DEFAULT_EXIT_THRESHOLD = 4f
    }
}
