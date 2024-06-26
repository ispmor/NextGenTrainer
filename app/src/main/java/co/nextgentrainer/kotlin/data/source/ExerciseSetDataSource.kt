package co.nextgentrainer.kotlin.data.source

import co.nextgentrainer.kotlin.data.model.ExerciseSet
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ExerciseSetDataSource {
    private val database = Firebase.database(
        "https://nextgentrainer-c380e-default-rtdb.europe-west1.firebasedatabase.app/"
    )
        .getReference("ExerciseSet")

    suspend fun saveExerciseSet(exerciseSet: ExerciseSet): String {
        val key = database.push().key!!
        database.child(key).setValue(exerciseSet)
        return key
    }
}
