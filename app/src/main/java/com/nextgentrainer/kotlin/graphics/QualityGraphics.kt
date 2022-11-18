package com.nextgentrainer.kotlin.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.GraphicOverlay.Graphic
import com.nextgentrainer.kotlin.data.models.Repetition
import java.util.Locale

class QualityGraphics(
    overlay: GraphicOverlay,
    private val repetition: Repetition?
) : Graphic(overlay) {
    private val whitePaint: Paint = Paint()
    private val backgroundPaint: Paint = Paint()
    private val greenPaint: Paint = Paint()
    val height = overlay.imageHeight
    val width = overlay.imageWidth

    init {
        whitePaint.color = Color.WHITE
        whitePaint.textSize = TEXT_SIZE
        backgroundPaint.color = Color.BLACK
        backgroundPaint.alpha = 50
        greenPaint.color = Color.rgb(43, 255, 0)
    }

    override fun draw(canvas: Canvas) {
        val repsNumber = repetition?.repetitionCounter?.numRepeats.toString()

        whitePaint.textSize = BIG_TEXT
        canvas.drawText(
            String.format(Locale.getDefault(), "Rep: %s", repsNumber),
            75f,
            canvas.height * 0.20f,
            whitePaint
        )
        drawQuality(canvas)
    }

    private fun drawQuality(canvas: Canvas) {
        var qualityLocationY = canvas.height * 0.75f
        val corners = floatArrayOf(
            80f,
            80f, // Top left radius in px
            80f,
            80f, // Top right radius in px
            80f,
            80f, // Bottom right radius in px
            80f,
            80f // Bottom left radius in px
        )

        val path = Path()
        path.addRoundRect(
            RectF(
                0f,
                canvas.height * 0.70f,
                canvas.width.toFloat(),
                canvas.height.toFloat() * 0.975f
            ),
            corners,
            Path.Direction.CW
        )
        canvas.drawPath(path, backgroundPaint)

        repetition?.quality?.qualityFeatures!!
            .forEach {
                canvas.drawText(
                    String.format(
                        Locale.getDefault(),
                        "%s",
                        it.name
                    ),
                    ZER0_F + 150,
                    qualityLocationY,
                    whitePaint
                )
                if (it.isValid) {
                    canvas.drawCircle(75f, qualityLocationY - TEXT_SIZE / 2, 30f, greenPaint)
                }
                qualityLocationY += QUALITY_Y_INTERVAL
            }
    }

    companion object {
        private const val TEXT_SIZE = 30.0f
        private const val QUALITY_Y_INTERVAL = 100
        private const val BIG_TEXT = 96.0f
        private const val ZER0_F = 0f
    }
}
