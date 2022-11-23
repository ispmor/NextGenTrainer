package com.nextgentrainer.kotlin.data.model

import java.util.Date

data class User(
    val login: String,
    val email: String,
    val password: String,
    val age: Int,
    val weight: Double?,
    val height: Int?,
    val gender: String?,
    val plan: String,
    val active: Boolean,
    val lastPayment: Date?
)
