package pl.koain.sports.maestry.demo.java.utils

class ExerciseSet(val setNumber: Int) {
    val repetitions: MutableList<Repetition>

    init {
        repetitions = ArrayList()
    }

    fun addRepetition(rep: Repetition) {
        repetitions.add(rep)
    }
}