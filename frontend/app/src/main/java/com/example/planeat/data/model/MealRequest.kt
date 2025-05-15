package com.example.planeat.data.model

data class MealRequest(
    val user_email: String,
    val age: Int,
    val gender: Int,
    val height: Double,
    val weight: Double,
    val can_cook: Int,
    val meals_per_day: Int,
    val hunger_cycle_min: Int,
    val allergies: String,
    val available_ingredients: Array<String>
)