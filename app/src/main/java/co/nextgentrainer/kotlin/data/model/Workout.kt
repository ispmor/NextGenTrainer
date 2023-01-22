package co.nextgentrainer.kotlin.data.model

data class Workout(
    val userId: String = "",
    val workoutId: String = "",
    val timestampMillis: Long = System.currentTimeMillis(),
    val sets: List<ExerciseSet> = listOf(),
    val isBest: Boolean = false
)
