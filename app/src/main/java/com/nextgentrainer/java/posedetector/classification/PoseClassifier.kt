package com.nextgentrainer.java.posedetector.classification

import android.util.Pair
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import java.util.PriorityQueue
import kotlin.collections.ArrayList

/**
 * Classifies {link Pose} based on given [PoseSample]s.
 *
 *
 * Inspired by K-Nearest Neighbors Algorithm with outlier filtering.
 * https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm
 */
class PoseClassifier @JvmOverloads constructor(
        private val poseSamples: List<PoseSample>,
        private val maxDistanceTopK: Int = MAX_DISTANCE_TOP_K,
        private val meanDistanceTopK: Int = MEAN_DISTANCE_TOP_K,
        private val axesWeights: PointF3D = AXES_WEIGHTS) {

    fun confidenceRange(): Int {
        return maxDistanceTopK.coerceAtMost(meanDistanceTopK)
    }

    fun classify(pose: Pose): ClassificationResult {
        return classify(extractPoseLandmarks(pose))
    }

    private fun classify(landmarks: List<PointF3D>): ClassificationResult {
        val result = ClassificationResult()
        if (landmarks.isEmpty()) {
            return result
        }

        // We do flipping on X-axis so we are horizontal (mirror) invariant.
        val flippedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
        Utils.multiplyAll(flippedLandmarks, PointF3D.from(-1f, 1f, 1f))
        val embedding = PoseEmbedding.getPoseEmbedding(landmarks)
        val flippedEmbedding = PoseEmbedding.getPoseEmbedding(flippedLandmarks)



        val maxDistances = PriorityQueue(
                maxDistanceTopK) {
            o1: Pair<PoseSample, Float?>,
            o2: Pair<PoseSample, Float?> -> -(o1.second!!).compareTo(o2.second!!) }
        for (poseSample in poseSamples) {
            val sampleEmbedding = poseSample.embedding
            var originalMax = 0f
            var flippedMax = 0f
            embedding.indices.forEach{
                originalMax = originalMax.coerceAtLeast(
                        Utils.maxAbs(
                                Utils.multiply(
                                        Utils.subtract(
                                                embedding[it], sampleEmbedding!![it]
                                        ), axesWeights)))
                flippedMax = flippedMax.coerceAtLeast(Utils.maxAbs(
                        Utils.multiply(
                                Utils.subtract(flippedEmbedding[it], sampleEmbedding[it]),
                                axesWeights)))
            }
            // Set the max distance as min of original and flipped max distance.
            maxDistances.add(Pair(poseSample, Math.min(originalMax, flippedMax)))
            // We only want to retain top n so pop the highest distance.
            if (maxDistances.size > maxDistanceTopK) {
                maxDistances.poll()
            }
        }

        // Keeps higher mean distances on top so we can pop it when top_k size is reached.
        val meanDistances = PriorityQueue(
                meanDistanceTopK) {
            o1: Pair<PoseSample, Float?>, o2: Pair<PoseSample, Float?> ->
            -java.lang.Float.compare(o1.second!!, o2.second!!) }
        // Retrive top K poseSamples by least mean distance to remove outliers.
        for (sampleDistances in maxDistances) {
            val poseSample = sampleDistances.first
            val sampleEmbedding = poseSample.embedding
            var originalSum = 0f
            var flippedSum = 0f
            for (i in embedding.indices) {
                originalSum += Utils.sumAbs(Utils.multiply(
                        Utils.subtract(embedding[i], sampleEmbedding!![i]), axesWeights))
                flippedSum += Utils.sumAbs(
                        Utils.multiply(
                                Utils.subtract(
                                        flippedEmbedding[i], sampleEmbedding[i]), axesWeights))
            }
            // Set the mean distance as min of original and flipped mean distances.
            val meanDistance = Math.min(originalSum, flippedSum) / (embedding.size * 2)
            meanDistances.add(Pair(poseSample, meanDistance))
            // We only want to retain top k so pop the highest mean distance.
            if (meanDistances.size > meanDistanceTopK) {
                meanDistances.poll()
            }
        }
        for (sampleDistances in meanDistances) {
            val className = sampleDistances.first.className
            result.incrementClassConfidence(className)
        }
        return result
    }

    companion object {
        private const val TAG = "PoseClassifier"
        private const val MAX_DISTANCE_TOP_K = 20
        private const val MEAN_DISTANCE_TOP_K = 10

        // Note Z has a lower weight as it is generally less accurate than X & Y.
        private val AXES_WEIGHTS = PointF3D.from(1f, 1f, 0.2f)
        private fun extractPoseLandmarks(pose: Pose): List<PointF3D> {
            val landmarks: MutableList<PointF3D> = ArrayList()
            for (poseLandmark in pose.allPoseLandmarks) {
                landmarks.add(poseLandmark.position3D)
            }
            return landmarks
        }
    }
}
