package com.nextgentrainer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.nextgentrainer.GraphicOverlay.Graphic

/**
 * Graphic instance for rendering inference info (latency, FPS, resolution) in an overlay view.
 */
class InferenceInfoGraphic(
        private val overlay: GraphicOverlay,
        private val frameLatency: Long,
        private val detectorLatency: Long,
        // Only valid when a stream of input images is being processed. Null for single image mode.
        private val framesPerSecond: Int?) : Graphic(overlay) {
    private val textPaint: Paint = Paint()
    private var showLatencyInfo = true

    init {
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        textPaint.setShadowLayer(RADIUS_SIZE, ZERO_F, ZERO_F, Color.BLACK)
        postInvalidate()
    }


    @Synchronized
    override fun draw(canvas: Canvas) {
        val x = TEXT_SIZE * HALF
        val y = TEXT_SIZE * ONE_AND_A_HALF
        canvas.drawText(
                "InputImage size: " + overlay.imageHeight + "x" + overlay.imageWidth,
                x,
                y,
                textPaint)
        if (!showLatencyInfo) {
            return
        }
        // Draw FPS (if valid) and inference latency
        if (framesPerSecond != null) {
            canvas.drawText(
                    "FPS: $framesPerSecond, Frame latency: $frameLatency ms",
                    x,
                    y + TEXT_SIZE,
                    textPaint)
        } else {
            canvas.drawText("Frame latency: $frameLatency ms", x, y + TEXT_SIZE, textPaint)
        }
        canvas.drawText(
                "Detector latency: $detectorLatency ms", x, y + TEXT_SIZE * DOUBLE, textPaint)
    }

    companion object {
        private const val TEXT_COLOR = Color.WHITE
        private const val TEXT_SIZE = 60.0f
        private const val RADIUS_SIZE = 5.0f
        private const val ZERO_F = 0.0f
        private const val HALF = 0.5f
        private const val DOUBLE = 2
        private const val ONE_AND_A_HALF = 1.5F
    }
}
