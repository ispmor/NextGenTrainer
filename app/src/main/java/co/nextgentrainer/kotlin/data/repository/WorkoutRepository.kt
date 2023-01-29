package co.nextgentrainer.kotlin.data.repository

import android.util.Log
import co.nextgentrainer.kotlin.data.model.ExerciseSet
import co.nextgentrainer.kotlin.data.model.Workout
import co.nextgentrainer.kotlin.data.source.WorkoutSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WorkoutRepository(private val workoutDataSource: WorkoutSource) {
    lateinit var selectedWorkout: Workout
    lateinit var selectedSet: ExerciseSet

    private val latestNewsMutex = Mutex()

    // Cache of the latest news got from the network.
    private var workoutList: List<Workout> = emptyList()

    private var lastWorkout: Workout? = null

    suspend fun getAllWorkouts(refresh: Boolean = false): List<Workout> {
        if (refresh || workoutList.isEmpty()) {
            val workoutResults = workoutDataSource.loadWorkouts()
            // Thread-safe write to latestNews
            latestNewsMutex.withLock {
                this.workoutList = workoutResults
            }
        }

        return latestNewsMutex.withLock { this.workoutList }
    }

    suspend fun getAllUserWorkouts(userId: String): Task<DataSnapshot> {
        return workoutDataSource.getAllWorkoutsForUser(userId).orderByChild("timestampMillis").get()
    }

    fun setWorkoutList(workouts: List<Workout>) {
        workoutList = workouts
    }

    fun initLastWorkout(refresh: Boolean) {
        if (refresh || lastWorkout == null) {
            workoutDataSource.getLastWorkoutOnline(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                val value = it.getValue<HashMap<String, Any>>()
                val tmpKey = value?.keys?.first()
                if (tmpKey != null) {
                    val tmp = it.child(tmpKey).getValue<Workout>()
                    if (tmp!!.workoutId.isNotEmpty()) {
                        lastWorkout = tmp
                    }
                }
            }.addOnCompleteListener {
                Log.d("TAG", it.toString())
            }
        }
    }

    suspend fun addWorkout(workout: Workout) {
        workoutDataSource.saveWorkout(workout)
        this.lastWorkout = workout
    }

    suspend fun createNewWorkout(userId: String): Workout {
        return Workout(userId)
    }

    fun addExerciseSetToWorkout(set: ExerciseSet) {
        lastWorkout = if (
            lastWorkout != null &&
            lastWorkout?.timestampMillis!! < set.repetitions.last().timestamp.time
        ) {
            val newWorkout = Workout(
                lastWorkout!!.userId,
                lastWorkout!!.workoutId,
                lastWorkout!!.timestampMillis,
                lastWorkout!!.sets + set
            )

            workoutDataSource.updateWorkout(newWorkout)
        } else {
            workoutDataSource.saveNewWorkout(set)
        }
    }
}
