package com.nextgentrainer.java.utils

import com.google.mlkit.vision.common.PointF3D
import kotlin.math.atan

class LineEquation(val a: Float, val b: Float, val c: Float) {
    companion object {
        fun getLineBetweenPoints(p1: PointF3D, p2: PointF3D): LineEquation {
            val a = p2.y - p1.y
            val b = p2.x - p1.x
            val c = (p1.x - p2.x) * p1.y + (p2.y - p1.y) * p1.x
            return LineEquation(a, b, c)
        }

        fun getAngleBetweenTwoLines(l1: LineEquation, l2: LineEquation): Double {
            val numerator = l2.a * l1.b - l1.a * l2.b
            val denominator = l1.a * l2.a + l1.b * l2.b
            return Math.toDegrees(atan((numerator / denominator).toDouble()))
        }
    }
}
