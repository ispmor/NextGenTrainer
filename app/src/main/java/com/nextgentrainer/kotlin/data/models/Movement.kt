package com.nextgentrainer.kotlin.data.models

import com.google.mlkit.vision.common.PointF3D

data class Movement(
    val leftHipMovement: ArrayList<PointF3D?>,
    val rightHipMovement: ArrayList<PointF3D?>,
    val leftKneeMovement: ArrayList<PointF3D?>,
    val rightKneeMovement: ArrayList<PointF3D?>,
    val leftAnkleMovement: ArrayList<PointF3D?>,
    val rightAnkleMovement: ArrayList<PointF3D?>,
    val leftShoulderMovement: ArrayList<PointF3D?>,
    val rightShoulderMovement: ArrayList<PointF3D?>,
    val leftElbowMovement: ArrayList<PointF3D?>,
    val rightElbowMovement: ArrayList<PointF3D?>,
    val leftWristMovement: ArrayList<PointF3D?>,
    val rightWristMovement: ArrayList<PointF3D?>,
    val leftToeMovement: ArrayList<PointF3D?>,
    val rightToeMovement: ArrayList<PointF3D?>,
    val leftHeelMovement: ArrayList<PointF3D?>,
    val rightHeelMovement: ArrayList<PointF3D?>,
    val mouthMovement: ArrayList<PointF3D?>,
    val leftKneeAngle: ArrayList<Double?>,
    val rightKneeAngle: ArrayList<Double?>,
    val hipsAngle: ArrayList<Double?>,
    val leftElbowAngle: ArrayList<Double?>,
    val rightElbowAngle: ArrayList<Double>,
    val leftElbowToTorsoAngle: ArrayList<Double?>,
    val rightElbowToTorsoAngle: ArrayList<Double?>,
    val distanceBetweenKnees: ArrayList<Double?>,
    val distanceBetweenAnkles: ArrayList<Double?>
)
