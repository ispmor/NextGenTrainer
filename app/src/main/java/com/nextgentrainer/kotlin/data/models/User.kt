package com.nextgentrainer.kotlin.data.models

data class User(
    val login: String,
    val email: String,
    val password: String,
    val age: Int,
    val weight: Double,
    val height: Int,
    val gender: String
)
