package com.nextgentrainer.java.utils

import android.content.Context
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.nextgentrainer.java.posedetector.MovementDescription
import com.nextgentrainer.java.utils.CameraActivityHelper.saveDataToCache
import java.util.Date
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object QualityDetector {
    private const val MOVEMENT_SPEED_OK = "movementSpeedOk"
    private const val SQUARE = 2.0
    private const val HALF = 0.5
    private const val MOVEMENT_SPEED_LOWER_THRESHOLD = 1.5
    private const val MOVEMENT_SPEED_UPPER_THRESHOLD = 3.0
    private const val ZERO = 0
    private const val MILL = 1000f
    private const val DEG_45 = 45
    private const val DEG_90 = 90
    private const val DEG_15 = 15
    private const val DISTANCE_MULTIPLIER = 1.2
    private const val UNIT = 1

    fun squatQuality(poseList: List<Pose>, posesTimestamps: List<Date>, context: Context): RepetitionQuality {
        /*
        What are the scoring rules for now:
        1) Movement should last 2-3s. Therefore poseList.size() / avgFPS = <2.0; 3.0>.
            anything less -> to fast, anything more -> to slow
        2) Knees should be the same width apart or wider as ankles, never substantially less.
        3) Angle between calf and tight should be around <=90 deg in the final moment (
        we can assume that if occurred at least once it is ok).
        4) If angle between calf and tight is >90 deg, the angle between torso and tights
        should >75 deg
        5) Y of hip and Y of arms should be substantially different
         */
        val results: MutableList<QualityFeature> = ArrayList()
        val movementSpeedOk: Boolean
        val kneesTrajectoryOk: Boolean
        val squatDeepEnough: Boolean
        val tightsTorsoAngleOkWhenSquatNotDeepEnough: Boolean
        val shoulderHipDistanceOk: Boolean
        val movementDescription = MovementDescription(poseList)
        val distanceBetweenAnklesAndKneesDiff: MutableList<Double> = ArrayList()
        val distanceBetweenAnklesAndKneesIsOk: MutableList<Boolean> = ArrayList()
        val squatDepthDeeperThan90deg: MutableList<Boolean> = ArrayList()
        val postureIsOk: MutableList<Boolean> = ArrayList()
        val shoulderHipDistance: MutableList<Boolean> = ArrayList()
        poseList.indices.forEach {
            distanceBetweenAnklesAndKneesDiff.add(
                abs(movementDescription.distanceBetweenKnees[it]!!) -
                    HALF * abs(movementDescription.distanceBetweenAnkles[it]!!)
            )
            distanceBetweenAnklesAndKneesIsOk.add(distanceBetweenAnklesAndKneesDiff[it] > ZERO)
            squatDepthDeeperThan90deg.add(
                movementDescription.leftKneeAngle[it]!! < ZERO &&
                    movementDescription.rightKneeAngle[it]!! < ZERO
            )
            postureIsOk.add(
                if (!squatDepthDeeperThan90deg[it]) {
                    movementDescription.torsoAngle[it]!! > DEG_45
                } else true
            )
            shoulderHipDistance.add(
                (
                    (
                        movementDescription.leftHipMovement[it]!!.y +
                            movementDescription.rightHipMovement[it]!!.y
                        ) * HALF - (
                        movementDescription.leftShoulderMovement[it]!!.y +
                            movementDescription.rightShoulderMovement[it]!!.y
                        ) * HALF
                    ) > ZERO
            )
        }
        val repTime = getRepTime(posesTimestamps)
        movementSpeedOk =
            MOVEMENT_SPEED_LOWER_THRESHOLD < repTime && repTime < MOVEMENT_SPEED_UPPER_THRESHOLD
        kneesTrajectoryOk = distanceBetweenAnklesAndKneesIsOk.stream().allMatch { i -> i }
        squatDeepEnough = squatDepthDeeperThan90deg.stream().anyMatch { i -> i }
        tightsTorsoAngleOkWhenSquatNotDeepEnough = postureIsOk.stream().allMatch { i -> i }
        shoulderHipDistanceOk = shoulderHipDistance.stream().allMatch { i -> i }
        results.add(
            QualityFeature(
                "kneesTrajectoryOk",
                kneesTrajectoryOk,
                distanceBetweenAnklesAndKneesDiff
            )
        )
        results.add(
            QualityFeature(
                "squatDeepEnough",
                squatDeepEnough,
                squatDepthDeeperThan90deg
            )
        )
        results.add(QualityFeature(MOVEMENT_SPEED_OK, movementSpeedOk, listOf(repTime)))
        results.add(
            QualityFeature(
                "tightsTorsoAngleOkWhenSquatNotDeepEnough",
                tightsTorsoAngleOkWhenSquatNotDeepEnough,
                postureIsOk
            )
        )
        results.add(
            QualityFeature(
                "shoulderHipDistanceOk",
                shoulderHipDistanceOk,
                shoulderHipDistance
            )
        )
        saveDataToCache(movementDescription.getAsJson() + "\n", "squatsCache.csv", context)
        return RepetitionQuality("squats", results)
    }

    fun pushupsQuality(poseList: List<Pose>, posesTimestamps: List<Date>): RepetitionQuality {
        /*
        1) Rep time should be between 1.5-3s
        2) Legs should be straight
        3) Leg-hips-arm should be somewhat straight or at least butt not lifted significantly
        4) Elbows should bend to 90 deg or less
        5) Elbows-torso angle should be between 15-60 deg
         */
        val results: MutableList<QualityFeature> = ArrayList()
        val movementSpeedOk: Boolean
        val legsAreStraight: Boolean
        val bodyIsStraight: Boolean
        val elbowsBentBelow90deg: Boolean
        val elbowsPositionRelativeToTorsoOk: Boolean
        val movementDescription = MovementDescription(poseList)
        val repTime = getRepTime(posesTimestamps)
        val areLegsStraight: MutableList<Boolean> = ArrayList()
        val isBodyProperlyAligned: MutableList<Boolean> = ArrayList()
        val elbowsBentTo90: MutableList<Double> = ArrayList()
        val avgAngleBetweenTorsoAndElbows: MutableList<Double> = ArrayList()
        poseList.indices.forEach {
            areLegsStraight.add(
                abs(
                    (
                        movementDescription.leftKneeAngle[it]!! +
                            movementDescription.rightKneeAngle[it]!!
                        ) * HALF
                ) > ZERO
            )
            isBodyProperlyAligned.add(abs(movementDescription.torsoAngle[it]!!) < DEG_15)
            elbowsBentTo90.add(abs(movementDescription.leftElbowAngle[it]!!))
            avgAngleBetweenTorsoAndElbows.add(
                (
                    abs(movementDescription.leftElbowToTorsoAngle[it]!!) +
                        abs(movementDescription.rightElbowToTorsoAngle[it]!!)
                    ) * HALF
            )
        }
        movementSpeedOk = MOVEMENT_SPEED_LOWER_THRESHOLD < repTime &&
            repTime < MOVEMENT_SPEED_UPPER_THRESHOLD
        legsAreStraight = areLegsStraight.stream().allMatch { i -> i }
        bodyIsStraight = isBodyProperlyAligned.stream().allMatch { i -> i }
        elbowsBentBelow90deg = elbowsBentTo90.stream().anyMatch {
            value: Double ->
            value > DEG_90 - DEG_15
        }
        elbowsPositionRelativeToTorsoOk = avgAngleBetweenTorsoAndElbows.stream().allMatch {
            value: Double ->
            value > DEG_15 && value < DEG_90
        }
        results.add(QualityFeature("movementSpeedOk", movementSpeedOk, listOf(repTime)))
        results.add(QualityFeature("legsAreStraight", legsAreStraight, areLegsStraight))
        results.add(
            QualityFeature(
                "bodyIsStraight",
                bodyIsStraight,
                movementDescription.torsoAngle
            )
        )
        results.add(
            QualityFeature("elbowsBentBelow90deg", elbowsBentBelow90deg, elbowsBentTo90)
        )
        results.add(
            QualityFeature(
                "elbowsPositionRelativeToTorsoOk",
                elbowsPositionRelativeToTorsoOk,
                avgAngleBetweenTorsoAndElbows
            )
        )
        return RepetitionQuality("pushups", results)
    }

    fun pullupsQuality(poseList: List<Pose>, posesTimestamps: List<Date>): RepetitionQuality {
        /*
        1) Rep speed MOVEMENT_SPEED_LOWER_THRESHOLD < x < 3 s
        2) No kipping (torso - legs) angle should always be close to ZERO - straight line or
        negative, when back arched
        3) Chin should go above the bar
        4) Elbows should be straight at the bottom
        5) ----> Pullups are the best exercise, so you have 1 star for free just doing them.
        */
        val results: MutableList<QualityFeature> = ArrayList()
        val movementSpeedOk: Boolean
        val freeStar = true
        val noKipping: Boolean
        val chinAboveTheBar: Boolean
        val elbowsStraightAtTheBottom: Boolean
        val movementDescription = MovementDescription(poseList)
        val torsoAngle = movementDescription.torsoAngle
        val leftKneeAngle = movementDescription.leftKneeAngle
        val rightKneeAngle = movementDescription.rightKneeAngle
        val legsAndTorsoStraightMoreOrLess: MutableList<Boolean> = ArrayList()
        val mouthAboveWrist: MutableList<Boolean> = ArrayList()
        val repTime = getRepTime(posesTimestamps)
        for (i in poseList.indices) {
            legsAndTorsoStraightMoreOrLess.add(
                abs(torsoAngle[i]!!) < DEG_45 &&
                    abs((leftKneeAngle[i]!! + rightKneeAngle[i]!!) * HALF) < DEG_45
            )
            mouthAboveWrist.add(
                movementDescription.mouthMovement[i]!!.y -
                    movementDescription.rightWristMovement[i]!!.y < ZERO
            )
        }
        noKipping = legsAndTorsoStraightMoreOrLess.stream().allMatch { i -> i }
        chinAboveTheBar = mouthAboveWrist.stream().anyMatch { i -> i }
        elbowsStraightAtTheBottom = movementDescription.leftElbowAngle.stream().anyMatch {
            value: Double? ->
            abs(value!!) < DEG_15
        }
        movementSpeedOk = MOVEMENT_SPEED_LOWER_THRESHOLD < repTime &&
            repTime < MOVEMENT_SPEED_UPPER_THRESHOLD
        results.add(QualityFeature("movementSpeedOk", movementSpeedOk, listOf(repTime)))
        results.add(QualityFeature("freeStar", freeStar, listOf(freeStar)))
        results.add(QualityFeature("noKipping", noKipping, legsAndTorsoStraightMoreOrLess))
        results.add(QualityFeature("chinAboveTheBar", chinAboveTheBar, mouthAboveWrist))
        results.add(
            QualityFeature(
                "elbowsStraightAtTheBottom",
                elbowsStraightAtTheBottom,
                movementDescription.leftElbowAngle
            )
        )
        return RepetitionQuality("pullups", results)
    }

    fun situpsQuality(poseList: List<Pose>, posesTimestamps: List<Date>): RepetitionQuality {
        /*
        1) knee angle should remain the same for the whole movement
        2) Wrists should be close to the head than to the angles
        3) Ankles should be at the same height more or less as the hips
        4) Repetition speed betwee MOVEMENT_SPEED_LOWER_THRESHOLD-3 s
        5) ... free star for now
         */
        val results: MutableList<QualityFeature> = ArrayList()
        val movementSpeedOk: Boolean
        val wristsNearHead: Boolean
        val anklesLevelWithHip: Boolean
        val kneesInStablePosition: Boolean
        val freeStar = true
        val movementDescription = MovementDescription(poseList)
        val wristsToHeadDistanceIsGreaterThanToAnkles: MutableList<Boolean> = ArrayList()
        val anklesMoreOrLessLevelWithHeap: MutableList<Boolean> = ArrayList()
        val repTime = getRepTime(posesTimestamps)
        for (i in poseList.indices) {
            wristsToHeadDistanceIsGreaterThanToAnkles.add(
                getDistanceBetween3dPoints(
                    movementDescription.mouthMovement[i],
                    movementDescription.leftWristMovement[i]!!
                ) < getDistanceBetween3dPoints(
                    movementDescription.leftAnkleMovement[i]!!,
                    movementDescription.leftWristMovement[i]!!
                )
            )
            anklesMoreOrLessLevelWithHeap.add(
                movementDescription.rightAnkleMovement[i]!!.y -
                    DISTANCE_MULTIPLIER
                    * movementDescription.rightHipMovement[i]!!.y < ZERO
            )
        }
        kneesInStablePosition = calculateSD(movementDescription.rightKneeAngle) < DEG_15
        wristsNearHead = wristsToHeadDistanceIsGreaterThanToAnkles.stream().allMatch { i -> i }
        anklesLevelWithHip = anklesMoreOrLessLevelWithHeap.stream().allMatch { i -> i }
        movementSpeedOk = MOVEMENT_SPEED_LOWER_THRESHOLD < repTime &&
            repTime < MOVEMENT_SPEED_UPPER_THRESHOLD
        results.add(QualityFeature("movementSpeedOk", movementSpeedOk, listOf(repTime)))
        results.add(QualityFeature("freeStar", freeStar, listOf(freeStar)))
        results.add(
            QualityFeature(
                "kneesInStablePosition",
                kneesInStablePosition,
                movementDescription.rightKneeAngle
            )
        )
        results.add(
            QualityFeature(
                "wristsNearHead",
                wristsNearHead,
                wristsToHeadDistanceIsGreaterThanToAnkles
            )
        )
        results.add(
            QualityFeature(
                "anklesLevelWithHip",
                anklesLevelWithHip,
                anklesMoreOrLessLevelWithHeap
            )
        )
        return RepetitionQuality("situps", results)
    }

    fun allExcerciseQuality(posesTimestamps: List<Date>): RepetitionQuality {
        /*
        dummy for speed only
         */

        val results: MutableList<QualityFeature> = ArrayList()
        val movementSpeedOk: Boolean
        val repTime = getRepTime(posesTimestamps)
        movementSpeedOk = MOVEMENT_SPEED_LOWER_THRESHOLD < repTime &&
            repTime < MOVEMENT_SPEED_UPPER_THRESHOLD
        results.add(QualityFeature("movementSpeedOk", movementSpeedOk, listOf(repTime)))
        return RepetitionQuality("all", results)
    }

    fun getRepTime(posesTimestamps: List<Date>): Float {
        return (posesTimestamps[posesTimestamps.size - UNIT].time - posesTimestamps[ZERO].time) / MILL
    }

    fun getDistanceBetween3dPoints(p1: PointF3D?, p2: PointF3D): Double {
        val x1 = p1!!.x
        val y1 = p1.y
        val z1 = p1.z
        val x2 = p2.x
        val y2 = p2.y
        val z2 = p2.z
        return (
            (x2 - x1).toDouble().pow(SQUARE) +
                (y2 - y1).toDouble().pow(SQUARE) +
                (z2 - z1).toDouble().pow(SQUARE)
            ).pow(HALF)
    }

    private fun calculateSD(numArray: ArrayList<Double?>): Double {
        val sum = numArray.reduce { acc, num -> num?.let { acc?.plus(it) } }
        val length = numArray.size
        val mean = sum?.times(UNIT / length)
        val standardDeviation = numArray.reduce {
            acc, num ->
            acc?.plus(((num?.minus(mean!!) ?: ZERO) as Double).pow(SQUARE))
        }

        return if (standardDeviation != null) {
            sqrt(standardDeviation / length)
        } else {
            ZERO.toDouble()
        }
    }
}
