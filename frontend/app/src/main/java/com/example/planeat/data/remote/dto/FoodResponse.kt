package com.example.planeat.data.remote.dto

// Ingredient class definition
data class Ingredient(
    val id: Int,
    val name: String,
    val possibleUnits: List<String>
)

// API response class for Food
data class FoodResponse(
    val success: Boolean,
    val message: String,
    val data: List<Ingredient>
)
