package com.example.planeat.data.remote.dto

data class SignupRequest(
    val idToken: String,
    val name: String,
    val gender: String,
    val age: Int,
    val height: Int,
    val weight: Int,
    val location: String,
    val mealsPerDay: Int,
    val hungerCycle: Int,
    val canCook: Boolean,
    val intolerances: List<String>
)