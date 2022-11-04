package com.nextgentrainer.java.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CompetitionSession(
    var uid: String? = "",
    var exercise: String? = null,
    var user1: String? = null,
    var reps1: Int? = null,
    var user2: String? = null,
    var reps2: Int? = null,
    var startDateMillis: Long? = null,
    var endDateMillis: Long? = null,
    var finished: Boolean = false
)
