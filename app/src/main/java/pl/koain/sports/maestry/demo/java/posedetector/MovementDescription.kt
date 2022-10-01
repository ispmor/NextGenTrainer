package pl.koain.sports.maestry.demo.java.posedetector

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import pl.koain.sports.maestry.demo.java.posedetector.classification.Utils
import pl.koain.sports.maestry.demo.java.utils.LineEquation
import pl.koain.sports.maestry.demo.java.utils.QualityDetector

class MovementDescription(val poseList: List<Pose>) {
    val leftHipMovement = ArrayList<PoseLandmark?>()
    val rightHipMovement = ArrayList<PoseLandmark?>()
    val leftKneeMovement = ArrayList<PoseLandmark?>()
    val rightKneeMovement = ArrayList<PoseLandmark?>()
    val leftAnkleMovement = ArrayList<PoseLandmark?>()
    val rightAnkleMovement = ArrayList<PoseLandmark?>()
    val leftShoulderMovement = ArrayList<PoseLandmark?>()
    val rightShoulderMovement = ArrayList<PoseLandmark?>()
    val leftElbowMovement = ArrayList<PoseLandmark?>()
    val rightElbowMovement = ArrayList<PoseLandmark?>()
    val leftWristMovement = ArrayList<PoseLandmark?>()
    val rightWristMovement = ArrayList<PoseLandmark?>()
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
    val torsoAngle = ArrayList<Double?>()
    val leftElbowAngle = ArrayList<Double?>()
    val rightElbowAngle = ArrayList<Double>()
    val leftElbowToTorsoAngle = ArrayList<Double?>()
    val rightElbowToTorsoAngle = ArrayList<Double?>()
    val distanceBetweenKnees = ArrayList<Double?>()
    val distanceBetweenAnkles = ArrayList<Double?>()

    init {
        var i = 0
        for (pose in poseList) {
            leftHipMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_HIP))
            rightHipMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP))
            leftKneeMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE))
            rightKneeMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE))
            leftAnkleMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE))
            rightAnkleMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE))
            leftShoulderMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER))
            rightShoulderMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER))
            leftElbowMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW))
            rightElbowMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW))
            leftWristMovement.add(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST))
            rightWristMovement.add(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST))
            mouthMovement.add(Utils.average(pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)!!.position3D, pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)!!.position3D))
            leftCalfLine.add(LineEquation.getLineBetweenPoints(leftKneeMovement[i]!!.position3D, leftAnkleMovement[i]!!.position3D))
            rightCalfLine.add(LineEquation.getLineBetweenPoints(rightKneeMovement[i]!!.position3D, rightAnkleMovement[i]!!.position3D))
            leftTightLine.add(LineEquation.getLineBetweenPoints(leftKneeMovement[i]!!.position3D, leftHipMovement[i]!!.position3D))
            rightTightLine.add(LineEquation.getLineBetweenPoints(rightKneeMovement[i]!!.position3D, rightHipMovement[i]!!.position3D))
            leftTorsoLine.add(LineEquation.getLineBetweenPoints(leftHipMovement[i]!!.position3D, leftShoulderMovement[i]!!.position3D))
            rightTorsoLine.add(LineEquation.getLineBetweenPoints(rightHipMovement[i]!!.position3D, rightShoulderMovement[i]!!.position3D))
            leftShoulderLine.add(LineEquation.getLineBetweenPoints(leftShoulderMovement[i]!!.position3D, leftElbowMovement[i]!!.position3D))
            rightShoulderLine.add(LineEquation.getLineBetweenPoints(rightShoulderMovement[i]!!.position3D, rightElbowMovement[i]!!.position3D))
            leftForearmLine.add(LineEquation.getLineBetweenPoints(leftElbowMovement[i]!!.position3D, leftWristMovement[i]!!.position3D))
            rightForearmLine.add(LineEquation.getLineBetweenPoints(rightElbowMovement[i]!!.position3D, rightWristMovement[i]!!.position3D))
            leftKneeAngle.add(LineEquation.getAngleBetweenTwoLines(leftCalfLine[i], leftTightLine[i]))
            rightKneeAngle.add(LineEquation.getAngleBetweenTwoLines(rightCalfLine[i], rightTightLine[i]))
            torsoAngle.add((LineEquation.getAngleBetweenTwoLines(rightTightLine[i], rightTorsoLine[i]) + LineEquation.getAngleBetweenTwoLines(leftTightLine[i], leftTorsoLine[i])) / 2)
            leftElbowAngle.add(LineEquation.getAngleBetweenTwoLines(leftShoulderLine[i], leftForearmLine[i]))
            rightElbowAngle.add(LineEquation.getAngleBetweenTwoLines(rightShoulderLine[i], rightForearmLine[i]))
            leftElbowToTorsoAngle.add(LineEquation.getAngleBetweenTwoLines(leftShoulderLine[i], leftTorsoLine[i]))
            rightElbowToTorsoAngle.add(LineEquation.getAngleBetweenTwoLines(rightShoulderLine[i], rightTorsoLine[i]))
            distanceBetweenKnees.add(QualityDetector.getDistanceBetween3dPoints(leftKneeMovement[i]!!.position3D, rightKneeMovement[i]!!.position3D))
            distanceBetweenAnkles.add(QualityDetector.getDistanceBetween3dPoints(leftAnkleMovement[i]!!.position3D, rightAnkleMovement[i]!!.position3D))
            i += 1
        }
    }

}