package com.example.planeat.data.model

data class UpdateUserProfile(
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
