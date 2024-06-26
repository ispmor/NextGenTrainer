package co.nextgentrainer.kotlin.data.model

data class ExerciseSet(
    val userId: String = "",
    val exerciseName: String = "",
    val repetitions: List<Repetition> = listOf(),
    var isBest: Boolean = false
)
