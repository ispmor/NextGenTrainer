package com.nextgentrainer.java.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.nextgentrainer.GraphicOverlay
import com.nextgentrainer.GraphicOverlay.Graphic
import com.nextgentrainer.java.utils.Repetition

class QualityGraphics(overlay: GraphicOverlay, private val repetition: Repetition?) : Graphic(overlay) {
    private val whitePaint: Paint

    init {
        whitePaint = Paint()
        whitePaint.color = Color.WHITE
        whitePaint.textSize = TEXT_SIZE
    }

    override fun draw(canvas: Canvas) {
        val repsNumber = repetition?.repetitionCounter?.numRepeats.toString()
        repetition?.quality?.let { String.format("QUALITY: %s", it.quality) }?.let { canvas.drawText(it, 0f, canvas.height / 4f, whitePaint) }
        whitePaint.textSize = 60f
        canvas.drawText(String.format("%s", repsNumber), canvas.width / 2f, canvas.height / 2f, whitePaint)
        drawQuality(canvas)
    }

    private fun drawQuality(canvas: Canvas) {
        var i = 200
        for (qualityFeature in repetition?.quality?.qualityFeatures!!) {
            canvas.drawText(String.format("%s: %s", qualityFeature.name, qualityFeature!!.isValid), 0f, i.toFloat(), whitePaint)
            i += 100
        }
    }

    companion object {
        private const val TEXT_SIZE = 30.0f
    }
}