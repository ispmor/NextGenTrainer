package com.nextgentrainer.kotlin.data.model

import com.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date

data class Repetition(
    val poseName: String? = "",
    val confidence: Float = 0.0f,
    val repetitionCounter: RepetitionCounter? = RepetitionCounter(),
    val quality: RepetitionQuality? = RepetitionQuality(),
    var timestamp: Date = Date(),
    val userId: String = "",
    val repetitionId: String = "",
    var webp: String = "",
    var absoluteLocalPath: String = "",
    var isBest: Boolean = false

)
