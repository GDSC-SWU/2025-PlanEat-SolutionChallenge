package com.example.planeat.data.model

data class MealRecommendationResponse(
    val label: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val letter: String
)