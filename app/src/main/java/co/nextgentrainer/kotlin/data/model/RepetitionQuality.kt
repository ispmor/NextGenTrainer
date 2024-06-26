package co.nextgentrainer.kotlin.data.model

import java.util.stream.Collectors

class RepetitionQuality(
    val exerciseName: String = "",
    val qualityFeatures: List<QualityFeature> = listOf(),
    val movementId: String = ""
) {

    val quality: Float
        get() {
            var sum = 0
            for (feature in qualityFeatures) {
                sum += if (feature.valid) 1 else 0
            }
            return sum.toFloat()
        }

    override fun toString(): String {
        return "{" +
            "\"exerciseName\":" +
            "\"" + exerciseName + "\"" +
            ", \"qualityScore\": " + quality +
            ", \"qualityFeatures\": " +
            qualityFeatures
                .stream()
                .map { obj: QualityFeature ->
                    obj.toString()
                }.collect(Collectors.toList()) +
            ", \"movementId\": " + movementId +
            "}"
    }
}
