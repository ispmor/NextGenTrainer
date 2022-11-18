package com.nextgentrainer.kotlin.data.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nextgentrainer.R
import com.nextgentrainer.kotlin.data.models.CompeteSession
import java.util.Date

class CompeteSessionRepository(private val context: Context) {

    private val database: DatabaseReference = Firebase.database(context.getString(R.string.database_url))
        .getReference(context.getString(R.string.movement))

    fun createNewSession(exercise: String?, user1: String?): String {
        val keyTmp = database.push().key
        if (keyTmp == null) {
            Log.w(TAG, "Couldn't get push key for competitionsession")
            return ""
        }
        val session = CompeteSession(keyTmp, exercise, user1, startDateMillis = Date().time)
        database.child(keyTmp).setValue(session)

//        bindSessionToKey(keyTmp)
        return keyTmp
    }

    fun getCompeteSession(key: String): CompeteSession {
        var tmpKey: String
        var resultSession: CompeteSession = CompeteSession()
        database.orderByChild("finished").equalTo(false).limitToFirst(1).get()
            .addOnSuccessListener {
                val value = it.getValue<HashMap<String, Any>>()

                if (value.isNullOrEmpty() && value != null) {
                    val tmpSession = it.child(value.keys.first()).getValue<CompeteSession>()
                    tmpSession!!.user2 = "test-2USER"

                    tmpKey = key!!
                    updateSession(tmpSession)
                    resultSession = tmpSession
                } else if (key.isNullOrEmpty() && value == null) {
                    tmpKey = createNewSession("squats", "Test-USER1")
                }
            }

        return resultSession
    }

    private fun updateSession(
        session: CompeteSession
    ) {
        database.child(session.uid!!).setValue(session)
    }

//    private fun bindSessionToKey(bindingKey: String) {
//        val c = bindingKey
// //        database.child(bindingKey).addValueEventListener(object : ValueEventListener {
// //            override fun onDataChange(dataSnapshot: DataSnapshot) {
// //                val sessionTmp = dataSnapshot.getValue<CompeteSession>()
// //                if (sessionTmp != null) {
// //                    if (
// //                        bothUsersExist(sessionTmp) &&
// //                        sessionTmp.endDateMillis == null &&
// //                        notStartedYet
// //                    ) {
// //                        countdownTextView.visibility = View.VISIBLE
// //                        timer.start()
// //                        againstTextView.visibility = View.INVISIBLE
// //                    }
// //
// //                    session = sessionTmp
// //
// //                    if (sessionTmp.finished) {
// //                        updateFinished()
// //                    }
// //                    Log.d(CompeteActivity.TAG, "Value is: $session")
// //                }
// //                Log.d(CompeteActivity.TAG, "Empty TMP session")
// //            }
// //
// //            override fun onCancelled(error: DatabaseError) {
// //                // Failed to read value
// //                Log.w(CompeteActivity.TAG, "Failed to read value.", error.toException())
// //            }
// //        })
//    }

    companion object {
        private const val TAG = "CompeteSessionRepository"
    }
}
