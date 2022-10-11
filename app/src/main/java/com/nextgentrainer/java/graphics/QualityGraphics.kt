package com.nextgentrainer.java.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.GraphicOverlay.Graphic
import com.nextgentrainer.java.utils.Repetition
import java.util.*

class QualityGraphics(
    overlay: GraphicOverlay,
    private val repetition: Repetition?
) : Graphic(overlay) {
    private val whitePaint: Paint = Paint()

    init {
        whitePaint.color = Color.WHITE
        whitePaint.textSize = TEXT_SIZE
    }

    override fun draw(canvas: Canvas) {
        val repsNumber = repetition?.repetitionCounter?.numRepeats.toString()
        repetition?.quality?.let {
            String.format(Locale.getDefault(), "QUALITY: %s", it.quality)
        }!!.let {
            canvas.drawText(it, ZER0_F, canvas.height / QUARTER, whitePaint)
        }
        whitePaint.textSize = BIG_TEXT
        canvas.drawText(
            String.format(Locale.getDefault(), "%s", repsNumber),
            canvas.width / HALF_2F,
            canvas.height / HALF_2F,
            whitePaint
        )
        drawQuality(canvas)
    }

    private fun drawQuality(canvas: Canvas) {
        var qualityLocationY = QUALITY_STARTING_LOCATION
        repetition?.quality?.qualityFeatures!!.forEach {
            canvas.drawText(
                String.format(
                    Locale.getDefault(),
                    "%s: %s",
                    it.name,
                    it.isValid
                ),
                ZER0_F,
                qualityLocationY,
                whitePaint
            )
            qualityLocationY += QUALITY_Y_INTERVAL
        }
    }

    companion object {
        private const val TEXT_SIZE = 30.0f
        private const val QUALITY_Y_INTERVAL = 100
        private const val BIG_TEXT = 60.0f
        private const val ZER0_F = 0f
        private const val HALF_2F = 2f
        private const val QUARTER = 4f
        private const val QUALITY_STARTING_LOCATION = 200f
    }
}
