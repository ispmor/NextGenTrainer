package com.nextgentrainer.kotlin.utils

import com.nextgentrainer.kotlin.data.model.Repetition

class ExerciseSet(val setNumber: Int) {
    val repetitions: MutableList<Repetition>

    init {
        repetitions = ArrayList()
    }

    fun addRepetition(rep: Repetition) {
        repetitions.add(rep)
    }
}
