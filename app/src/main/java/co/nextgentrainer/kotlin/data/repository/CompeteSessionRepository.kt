package co.nextgentrainer.kotlin.data.repository

import android.content.Context
import android.util.Log
import co.nextgentrainer.R
import co.nextgentrainer.kotlin.data.model.CompeteSession
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.Date

class CompeteSessionRepository(private val context: Context) {

    private val database: DatabaseReference = Firebase.database(context.getString(R.string.database_url))
        .getReference("CompetitionSession")
    private val user = Firebase.auth.currentUser!!

    fun createNewSession(exercise: String): String {
        val keyTmp = database.push().key
        if (keyTmp == null) {
            Log.w(TAG, "Couldn't get push key for competitionsession")
            return ""
        }
        val session = CompeteSession(
            keyTmp,
            exercise,
            user1 = Firebase.auth.currentUser!!.displayName!!,
            startDateMillis = Date().time
        )
        database.child(keyTmp).setValue(session)

        return keyTmp
    }

    fun getCompeteSessionReference(): Task<DataSnapshot> {
        return database.orderByChild("finished").equalTo(false).limitToFirst(1).get()
    }

    fun getCompeteSession(key: String): CompeteSession {
        var tmpKey: String
        var resultSession: CompeteSession = CompeteSession()
        database.child(user.uid).orderByChild("finished").equalTo(false).limitToFirst(1).get()
            .addOnSuccessListener {
                val value = it.getValue<HashMap<String, Any>>()

                if (value.isNullOrEmpty() && value != null) {
                    val tmpSession = it.child(value.keys.first()).getValue<CompeteSession>()
                    tmpSession!!.user2 = Firebase.auth.currentUser!!.displayName!!

                    tmpKey = key
                    updateSession(tmpSession)
                    resultSession = tmpSession
                } else if (key.isEmpty() && value == null) {
                    tmpKey = createNewSession("squats")
                }
            }

        return resultSession
    }

    fun updateSession(session: CompeteSession) {
        database.child(session.uid).setValue(session)
    }

    fun saveRepsForSessionAndUser(key: String, repsString: String, reps: Int) {
        database.child(key).child(repsString).setValue(reps)
    }

    fun setEndDateMillis(key: String, time: Long) {
        database.child(key).child("endDateMillis").setValue(time)
    }

    fun setFinished(key: String) {
        database.child(key).child("finished").setValue(true)
    }

    fun getSessionFromKey(key: String): DatabaseReference {
        return database.child(key)
    }

    fun setUserFinished(key: String, whichUserAmI: String) {
        database.child(key).child("${whichUserAmI}_finished").setValue(true)
    }

    companion object {
        private const val TAG = "CompeteSessionRepository"
    }
}
