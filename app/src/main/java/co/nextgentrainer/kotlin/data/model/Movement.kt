package co.nextgentrainer.kotlin.data.model

import java.util.Date

data class Movement(
    val leftHipMovement: ArrayList<Point> = arrayListOf(),
    val rightHipMovement: ArrayList<Point> = arrayListOf(),
    val leftKneeMovement: ArrayList<Point> = arrayListOf(),
    val rightKneeMovement: ArrayList<Point> = arrayListOf(),
    val leftAnkleMovement: ArrayList<Point> = arrayListOf(),
    val rightAnkleMovement: ArrayList<Point> = arrayListOf(),
    val leftShoulderMovement: ArrayList<Point> = arrayListOf(),
    val rightShoulderMovement: ArrayList<Point> = arrayListOf(),
    val leftElbowMovement: ArrayList<Point> = arrayListOf(),
    val rightElbowMovement: ArrayList<Point> = arrayListOf(),
    val leftWristMovement: ArrayList<Point> = arrayListOf(),
    val rightWristMovement: ArrayList<Point> = arrayListOf(),
    val leftToeMovement: ArrayList<Point> = arrayListOf(),
    val rightToeMovement: ArrayList<Point> = arrayListOf(),
    val leftHeelMovement: ArrayList<Point> = arrayListOf(),
    val rightHeelMovement: ArrayList<Point> = arrayListOf(),
    val mouthMovement: ArrayList<Point> = arrayListOf(),
    val leftKneeAngle: ArrayList<Double?> = arrayListOf(),
    val rightKneeAngle: ArrayList<Double?> = arrayListOf(),
    val hipsAngle: ArrayList<Double?> = arrayListOf(),
    val leftElbowAngle: ArrayList<Double?> = arrayListOf(),
    val rightElbowAngle: ArrayList<Double> = arrayListOf(),
    val leftElbowToTorsoAngle: ArrayList<Double?> = arrayListOf(),
    val rightElbowToTorsoAngle: ArrayList<Double?> = arrayListOf(),
    val distanceBetweenKnees: ArrayList<Double?> = arrayListOf(),
    val distanceBetweenAnkles: ArrayList<Double?> = arrayListOf(),
    val timestamps: List<Date> = listOf()
)
