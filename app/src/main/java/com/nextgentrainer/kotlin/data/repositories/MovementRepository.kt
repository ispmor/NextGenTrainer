package com.nextgentrainer.kotlin.data.repositories

import android.content.Context
import android.provider.Settings.Global.getString
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.nextgentrainer.R
import com.nextgentrainer.kotlin.data.models.Movement
import com.nextgentrainer.kotlin.posedetector.classification.Utils
import com.nextgentrainer.kotlin.utils.QualityDetector
import kotlin.math.abs

class MovementRepository(private val context: Context) {

    private val database = Firebase.database(context.getString(R.string.database_url))
        .getReference(context.getString(R.string.movement))

    fun saveMovement(movement: Movement): String {
        val key = database.push().key!!
        database.child(key).setValue(movement)
        return key
    }

    fun getMovement(key: String): Movement? {
        return database.child(key).get().result.getValue<Movement>()
    }

    fun getNewMovementFromPoseList(poseList: List<Pose>): Movement {
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
        val leftKneeAngle = ArrayList<Double?>()
        val rightKneeAngle = ArrayList<Double?>()
        val hipsAngle = ArrayList<Double?>()
        val leftElbowAngle = ArrayList<Double?>()
        val rightElbowAngle = ArrayList<Double>()
        val leftElbowToTorsoAngle = ArrayList<Double?>()
        val rightElbowToTorsoAngle = ArrayList<Double?>()
        val distanceBetweenKnees = ArrayList<Double?>()
        val distanceBetweenAnkles = ArrayList<Double?>()

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
        return Movement(
            leftHipMovement,
            rightHipMovement,
            leftKneeMovement,
            rightKneeMovement,
            leftAnkleMovement,
            rightAnkleMovement,
            leftShoulderMovement,
            rightShoulderMovement,
            leftElbowMovement,
            rightElbowMovement,
            leftWristMovement,
            rightWristMovement,
            leftToeMovement,
            rightToeMovement,
            leftHeelMovement,
            rightHeelMovement,
            mouthMovement,
            leftKneeAngle,
            rightKneeAngle,
            hipsAngle,
            leftElbowAngle,
            rightElbowAngle,
            leftElbowToTorsoAngle,
            rightElbowToTorsoAngle,
            distanceBetweenKnees,
            distanceBetweenAnkles
        )
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

    fun getAsJson(movement: Movement): String {
        val gson = Gson()
        return gson.toJson(movement)
    }
}
