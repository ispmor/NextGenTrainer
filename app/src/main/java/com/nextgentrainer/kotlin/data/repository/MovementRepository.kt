package com.nextgentrainer.kotlin.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.nextgentrainer.kotlin.data.model.Movement
import com.nextgentrainer.kotlin.data.model.Point
import com.nextgentrainer.kotlin.data.source.MovementFirebaseSource
import com.nextgentrainer.kotlin.posedetector.classification.Utils
import com.nextgentrainer.kotlin.utils.QualityDetector
import java.util.Date
import kotlin.math.abs

class MovementRepository {

    private val database = MovementFirebaseSource().database
    private val user = Firebase.auth.currentUser!!
    lateinit var selectedMovement: Movement
    lateinit var selectedMovementId: String

    fun saveMovement(movement: Movement): String {
        val key = database.child(user.uid).push().key!!
        database.child(user.uid).child(key).setValue(movement)
        return key
    }

    fun getMovement(key: String): Task<DataSnapshot> {
        return database.child(user.uid).child(key).get()
    }

    fun getNewMovementFromPoseList(poseList: List<Pose>, posesTimestamps: List<Date>): Movement {
        val leftHipMovement = ArrayList<Point>()
        val rightHipMovement = ArrayList<Point>()
        val leftKneeMovement = ArrayList<Point>()
        val rightKneeMovement = ArrayList<Point>()
        val leftAnkleMovement = ArrayList<Point>()
        val rightAnkleMovement = ArrayList<Point>()
        val leftShoulderMovement = ArrayList<Point>()
        val rightShoulderMovement = ArrayList<Point>()
        val leftElbowMovement = ArrayList<Point>()
        val rightElbowMovement = ArrayList<Point>()
        val leftWristMovement = ArrayList<Point>()
        val rightWristMovement = ArrayList<Point>()
        val leftToeMovement = ArrayList<Point>()
        val rightToeMovement = ArrayList<Point>()
        val leftHeelMovement = ArrayList<Point>()
        val rightHeelMovement = ArrayList<Point>()
        val mouthMovement = ArrayList<Point>()
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
            pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftHipMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightHipMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftKneeMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightKneeMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftAnkleMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightAnkleMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftShoulderMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightShoulderMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftElbowMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightElbowMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftWristMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightWristMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftToeMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightToeMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { leftHeelMovement.add(it) }

            pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)?.position3D?.let {
                Point(
                    it.x,
                    it.y,
                    it.z
                )
            }?.let { rightHeelMovement.add(it) }

            val mouthF3D = Utils.average(
                pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)!!
                    .position3D,
                pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)!!.position3D
            )
            mouthMovement.add(
                Point(
                    mouthF3D.x,
                    mouthF3D.y,
                    mouthF3D.z
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
                    leftKneeMovement[i],
                    rightKneeMovement[i]
                )
            )
            distanceBetweenAnkles.add(
                QualityDetector.getDistanceBetween3dPoints(
                    leftAnkleMovement[i],
                    rightAnkleMovement[i]
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
            distanceBetweenAnkles,
            posesTimestamps
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
