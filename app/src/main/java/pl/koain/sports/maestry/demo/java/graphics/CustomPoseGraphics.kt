package pl.koain.sports.maestry.demo.java.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import pl.koain.sports.maestry.demo.GraphicOverlay
import com.google.mlkit.vision.pose.Pose
import pl.koain.sports.maestry.demo.java.utils.Repetition
import pl.koain.sports.maestry.demo.GraphicOverlay.Graphic
import pl.koain.sports.maestry.demo.java.graphics.CustomPoseGraphics
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.common.PointF3D
import com.google.common.primitives.Ints
import java.util.ArrayList

/**
 * Draw the detected pose in preview.
 */
class CustomPoseGraphics(
        overlay: GraphicOverlay,
        private val pose: Pose,
        private val poseClassification: List<String>,
        repetition: Repetition?) : Graphic(overlay) {
    private var zMin = Float.MAX_VALUE
    private var zMax = Float.MIN_VALUE
    private val classificationTextPaint: Paint = Paint()
    private val whitePaint: Paint
    private val repetition: Repetition?
    private val visualizeZ = true
    private val rescaleZForVisualization = true

    init {
        classificationTextPaint.color = Color.WHITE
        classificationTextPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
        classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)
        whitePaint = Paint()
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        this.repetition = repetition
    }

    override fun draw(canvas: Canvas) {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }
        if (repetition != null && repetition.repetitionCounter != null) {
        }

        // Draw pose classification text.
        val classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f
        for (i in poseClassification.indices) {
            val classificationY = canvas.height - (POSE_CLASSIFICATION_TEXT_SIZE * 1.5f
                    * (poseClassification.size - i))
            canvas.drawText(
                    poseClassification[i],
                    classificationX,
                    classificationY,
                    classificationTextPaint)
        }

        // Draw all the points
        for (landmark in landmarks) {
            if (landmark.landmarkType > 10) { //skipping face
                drawPoint(canvas, landmark, whitePaint)
                if (visualizeZ && rescaleZForVisualization) {
                    zMin = Math.min(zMin, landmark.position3D.z)
                    zMax = Math.max(zMax, landmark.position3D.z)
                }
            }
        }
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        drawLine(canvas, leftShoulder, rightShoulder, whitePaint)
        drawLine(canvas, leftHip, rightHip, whitePaint)

        // Left body
        drawLine(canvas, leftShoulder, leftElbow, whitePaint)
        drawLine(canvas, leftElbow, leftWrist, whitePaint)
        drawLine(canvas, leftShoulder, leftHip, whitePaint)
        drawLine(canvas, leftHip, leftKnee, whitePaint)
        drawLine(canvas, leftKnee, leftAnkle, whitePaint)

        // Right body
        drawLine(canvas, rightShoulder, rightElbow, whitePaint)
        drawLine(canvas, rightElbow, rightWrist, whitePaint)
        drawLine(canvas, rightShoulder, rightHip, whitePaint)
        drawLine(canvas, rightHip, rightKnee, whitePaint)
        drawLine(canvas, rightKnee, rightAnkle, whitePaint)
    }

    fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint) {
        val point = landmark.position3D
        maybeUpdatePaintColor(paint, canvas, point.z)
        canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
    }

    fun drawLine(canvas: Canvas, startLandmark: PoseLandmark?, endLandmark: PoseLandmark?, paint: Paint) {
        val start = startLandmark!!.position3D
        val end = endLandmark!!.position3D

        // Gets average z for the current body line
        val avgZInImagePixel = (start.z + end.z) / 2
        maybeUpdatePaintColor(paint, canvas, avgZInImagePixel)
        canvas.drawLine(
                translateX(start.x),
                translateY(start.y),
                translateX(end.x),
                translateY(end.y),
                paint)
    }

    private fun maybeUpdatePaintColor(paint: Paint, canvas: Canvas, zInImagePixel: Float) {
        if (!visualizeZ) {
            return
        }

        // When visualizeZ is true, sets up the paint to different colors based on z values.
        // Gets the range of z value.
        val zLowerBoundInScreenPixel: Float
        val zUpperBoundInScreenPixel: Float
        if (rescaleZForVisualization) {
            zLowerBoundInScreenPixel = Math.min(-0.001f, scale(zMin))
            zUpperBoundInScreenPixel = Math.max(0.001f, scale(zMax))
        } else {
            // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
            val defaultRangeFactor = 1f
            zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
            zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
        }
        val zInScreenPixel = scale(zInImagePixel)
        if (zInScreenPixel < 0) {
            // Sets up the paint to draw the body line in red if it is in front of the z origin.
            // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
            // color. The larger the value is, the more red it will be.
            var v = (zInScreenPixel / zLowerBoundInScreenPixel * 255).toInt()
            v = Ints.constrainToRange(v, 0, 255)
            paint.setARGB(255, 255, 255 - v, 255 - v)
        } else {
            // Sets up the paint to draw the body line in blue if it is behind the z origin.
            // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
            // color. The larger the value is, the more blue it will be.
            var v = (zInScreenPixel / zUpperBoundInScreenPixel * 255).toInt()
            v = Ints.constrainToRange(v, 0, 255)
            paint.setARGB(255, 255 - v, 255 - v, 255)
        }
    }

    companion object {
        private const val TAG = "CustomPoseGraphics"
        private const val DOT_RADIUS = 8.0f
        private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private const val STROKE_WIDTH = 10.0f
        private const val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f
    }
}