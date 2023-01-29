package co.nextgentrainer.kotlin.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CompeteSession(
    var uid: String? = "",
    var exercise: String? = null,
    var user1: String? = null,
    var reps1: Int = 0,
    var user2: String? = null,
    var reps2: Int = 0,
    var startDateMillis: Long? = null,
    var endDateMillis: Long? = null,
    var finished: Boolean = false
)
