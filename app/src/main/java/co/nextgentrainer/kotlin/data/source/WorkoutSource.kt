package co.nextgentrainer.kotlin.data.source

import android.content.Context
import android.util.Log
import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.model.Workout
import com.google.android.gms.common.annotation.KeepName
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonStreamParser
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@KeepName
class WorkoutSource(val context: Context) {
    private val cacheFilename = "cache.csv"

    private val database =
        Firebase.database("https://nextgentrainer-c380e-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Workout")

    fun saveWorkout(workout: Workout) {
        val fileOutput: FileOutputStream =
            context.openFileOutput(cacheFilename, Context.MODE_APPEND)
        val gson = GsonBuilder().create()
        fileOutput.use { fos ->
            fos.write(gson.toJson(workout).toByteArray(StandardCharsets.UTF_8))
        }
    }

    fun loadWorkouts(): List<Workout> {
        val fileInput: FileInputStream = context.openFileInput(cacheFilename)
        fileInput.use { inputStreamFromFile ->
            InputStreamReader(inputStreamFromFile, StandardCharsets.UTF_8).use { reader ->
                try {
                    val gson = GsonBuilder().create()
                    val jsonStreamParserToObject = JsonStreamParser(reader)
                    val workouts: MutableList<Workout> = mutableListOf()
                    while (jsonStreamParserToObject.hasNext()) {
                        val singleJsonElement = jsonStreamParserToObject.next()
                        val workout = gson.fromJson(
                            singleJsonElement,
                            Workout::class.java
                        )
                        workouts.add(workout)
                    }

                    return workouts
                } catch (e: JsonIOException) {
                    Log.d(Companion.TAG, "Failed to open cache file")
                    return listOf()
                }
            }
        }
    }

    fun getLastWorkoutOnline(userId: String): Query {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateString = formatter.format(Date(System.currentTimeMillis()))
        val tdayMillis = formatter.parse(dateString)!!.time
        return database.child(userId)
            .orderByChild("timestampMillis")
            .startAt(tdayMillis.toDouble())
            .limitToLast(1)
    }

    fun updateWorkout(newWorkout: Workout): Workout {
        database.child(newWorkout.userId).child(newWorkout.workoutId).setValue(newWorkout)
        return newWorkout
    }

    fun saveNewWorkout(set: ExerciseSet): Workout {
        val key = database.child(set.userId).push().key!!
        val newWorkout = Workout(set.userId, key, sets = listOf(set))
        database.child(set.userId).child(key).setValue(newWorkout)
        saveWorkout(newWorkout)
        return newWorkout
    }

    fun getAllWorkoutsForUser(userId: String): Query {
        return database.child(userId)
    }

    companion object {
        private const val TAG: String = "WorkoutSource"
    }
}
