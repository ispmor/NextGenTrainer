package com.nextgentrainer.java.utils

import com.nextgentrainer.java.posedetector.classification.RepetitionCounter
import java.util.*

class Repetition {
    var id: UUID
    var poseName: String?
    var confidence: Float
    val repetitionCounter: RepetitionCounter?
    val quality: RepetitionQuality?
    var timestamp: Date

    constructor() {
        id = UUID.randomUUID()
        poseName = ""
        confidence = 0.0f
        repetitionCounter = null
        quality = null
        timestamp = Date()
    }

    constructor(repetitionCounter: RepetitionCounter?, quality: RepetitionQuality?, poseName: String?) {
        id = UUID.randomUUID()
        this.poseName = poseName
        confidence = 0.0f
        this.repetitionCounter = repetitionCounter
        this.quality = quality
        timestamp = Date()
    }
}
