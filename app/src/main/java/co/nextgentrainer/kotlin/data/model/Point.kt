package co.nextgentrainer.kotlin.data.model

import com.google.mlkit.vision.common.PointF3D

class Point(
    private var x: Float = 0f,
    private var y: Float = 0f,
    private var z: Float = 0f,
) : PointF3D() {

    override fun getX(): Float {
        return this.x
    }

    override fun getY(): Float {
        return this.y
    }

    override fun getZ(): Float {
        return this.z
    }

    fun setX(x: Float) {
        this.x = x
    }

    fun setY(y: Float) {
        this.y = y
    }

    fun setZ(z: Float) {
        this.z = z
    }
}
