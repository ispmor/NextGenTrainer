package com.nextgentrainer.kotlin.data.model

import com.nextgentrainer.kotlin.posedetector.classification.RepetitionCounter
import java.util.Date

data class Repetition(
    val poseName: String?,
    val confidence: Float,
    val repetitionCounter: RepetitionCounter?,
    val quality: RepetitionQuality?,
    var timestamp: Date = Date(),
    val userId: String
)
