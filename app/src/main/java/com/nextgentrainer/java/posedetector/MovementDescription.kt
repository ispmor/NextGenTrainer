package com.nextgentrainer.java.posedetector

import com.google.gson.Gson
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.nextgentrainer.java.posedetector.classification.Utils
import com.nextgentrainer.java.utils.LineEquation
import com.nextgentrainer.java.utils.QualityDetector
import kotlin.math.abs

class MovementDescription(val poseList: List<Pose>) {
    val leftHipMovement = ArrayList<PointF3D?>()
    val rightHipMovement = ArrayList<PointF3D?>()
    val leftKneeMovement = ArrayList<PointF3D?>()
    val rightKneeMovement = ArrayList<PointF3D?>()
    val leftAnkleMovement = ArrayList<PointF3D?>()
    val rightAnkleMovement = ArrayList<PointF3D?>()
    val leftShoulderMovement = ArrayList<PointF3D?>()
    val rightShoulderMovement = ArrayList<PointF3D?>()
    val leftElbowMovement = ArrayList<PointF3D?>()
    val rightElbowMovement = ArrayList<PointF3D?>()
    val leftWristMovement = ArrayList<PointF3D?>()
    val rightWristMovement = ArrayList<PointF3D?>()
    val leftToeMovement = ArrayList<PointF3D?>()
    val rightToeMovement = ArrayList<PointF3D?>()
    val leftHeelMovement = ArrayList<PointF3D?>()
    val rightHeelMovement = ArrayList<PointF3D?>()
    val mouthMovement = ArrayList<PointF3D?>()
    val leftCalfLine = ArrayList<LineEquation>()
    val rightCalfLine = ArrayList<LineEquation>()
    val leftTightLine = ArrayList<LineEquation>()
    val rightTightLine = ArrayList<LineEquation>()
    val leftTorsoLine = ArrayList<LineEquation>()
    val rightTorsoLine = ArrayList<LineEquation>()
    val leftShoulderLine = ArrayList<LineEquation>()
    val rightShoulderLine = ArrayList<LineEquation>()
    val leftForearmLine = ArrayList<LineEquation>()
    val rightForearmLine = ArrayList<LineEquation>()
    val leftKneeAngle = ArrayList<Double?>()
    val rightKneeAngle = ArrayList<Double?>()
    val hipsAngle = ArrayList<Double?>()
    val leftElbowAngle = ArrayList<Double?>()
    val rightElbowAngle = ArrayList<Double>()
    val leftElbowToTorsoAngle = ArrayList<Double?>()
    val rightElbowToTorsoAngle = ArrayList<Double?>()
    val distanceBetweenKnees = ArrayList<Double?>()
    val distanceBetweenAnkles = ArrayList<Double?>()

    init {
        var i = 0
        for (pose in poseList) {
            leftHipMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position3D)
            rightHipMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position3D)
            leftKneeMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position3D)
            rightKneeMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position3D)
            leftAnkleMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position3D)
            rightAnkleMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position3D)
            leftShoulderMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position3D)
            rightShoulderMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position3D)
            leftElbowMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position3D)
            rightElbowMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position3D)
            leftWristMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position3D)
            rightWristMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position3D)
            leftToeMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)?.position3D)
            rightToeMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)?.position3D)
            leftHeelMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)?.position3D)
            rightHeelMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)?.position3D)
            mouthMovement.add(
                Utils.average(
                    pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)!!
                        .position3D,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)!!.position3D
                )
            )
            leftCalfLine.add(
                LineEquation.getLineBetweenPoints(
                    leftKneeMovement[i]!!,
                    leftAnkleMovement[i]!!
                )
            )
            rightCalfLine.add(
                LineEquation.getLineBetweenPoints(
                    rightKneeMovement[i]!!,
                    rightAnkleMovement[i]!!
                )
            )
            leftTightLine.add(
                LineEquation.getLineBetweenPoints(
                    leftKneeMovement[i]!!,
                    leftHipMovement[i]!!
                )
            )
            rightTightLine.add(
                LineEquation.getLineBetweenPoints(
                    rightKneeMovement[i]!!,
                    rightHipMovement[i]!!
                )
            )
            leftTorsoLine.add(
                LineEquation.getLineBetweenPoints(
                    leftHipMovement[i]!!,
                    leftShoulderMovement[i]!!
                )
            )
            rightTorsoLine.add(
                LineEquation.getLineBetweenPoints(
                    rightHipMovement[i]!!,
                    rightShoulderMovement[i]!!
                )
            )
            leftShoulderLine.add(
                LineEquation.getLineBetweenPoints(
                    leftShoulderMovement[i]!!,
                    leftElbowMovement[i]!!
                )
            )
            rightShoulderLine.add(
                LineEquation.getLineBetweenPoints(
                    rightShoulderMovement[i]!!,
                    rightElbowMovement[i]!!
                )
            )
            leftForearmLine.add(
                LineEquation.getLineBetweenPoints(
                    leftElbowMovement[i]!!,
                    leftWristMovement[i]!!
                )
            )
            rightForearmLine.add(
                LineEquation.getLineBetweenPoints(
                    rightElbowMovement[i]!!,
                    rightWristMovement[i]!!
                )
            )
            leftKneeAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP)!!
                )
            )
            rightKneeAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)!!
                )
            )
            hipsAngle.add(
                (
                    getAngle(
                        pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)!!,
                        pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)!!,
                        pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!
                    ) + getAngle(
                        pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)!!,
                        pose.getPoseLandmark(PoseLandmark.LEFT_HIP)!!,
                        pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!
                    )
                    ) / 2
            )
            leftElbowAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)!!
                )
            )
            rightElbowAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)!!
                )
            )
            leftElbowToTorsoAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)!!,
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)!!
                )
            )
            rightElbowToTorsoAngle.add(
                getAngle(
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
                )
            )
            distanceBetweenKnees.add(
                QualityDetector.getDistanceBetween3dPoints(
                    leftKneeMovement[i]!!,
                    rightKneeMovement[i]!!
                )
            )
            distanceBetweenAnkles.add(
                QualityDetector.getDistanceBetween3dPoints(
                    leftAnkleMovement[i]!!,
                    rightAnkleMovement[i]!!
                )
            )
            i += 1
        }
    }

    fun getAsJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
            (
                kotlin.math.atan2(
                    lastPoint.position.y - midPoint.position.y,
                    lastPoint.position.x - midPoint.position.x
                ) - kotlin.math.atan2(
                    firstPoint.position.y - midPoint.position.y,
                    firstPoint.position.x - midPoint.position.x
                )
                ).toDouble()
        )
        result = abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }
}
