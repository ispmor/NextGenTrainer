package com.nextgentrainer.java.utils

class ExerciseSet(val setNumber: Int) {
    val repetitions: MutableList<Repetition>

    init {
        repetitions = ArrayList()
    }

    fun addRepetition(rep: Repetition) {
        repetitions.add(rep)
    }
}