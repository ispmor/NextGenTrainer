package co.nextgentrainer.kotlin.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CompeteSession(
    var uid: String = "",
    var exercise: String = "",
    var user1: String = "",
    var reps1: Int = 0,
    var user2: String = "",
    var reps2: Int = 0,
    var startDateMillis: Long = 0,
    var endDateMillis: Long = 0,
    var finished: Boolean = false,
    var user1Finished: Boolean = false,
    var user2Finished: Boolean = false
)
