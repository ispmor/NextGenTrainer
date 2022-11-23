package com.nextgentrainer.kotlin.data.model

class ExerciseSetOld(val setNumber: Int) {
    val repetitions: MutableList<Repetition>

    init {
        repetitions = ArrayList()
    }

    fun addRepetition(rep: Repetition) {
        repetitions.add(rep)
    }
}
