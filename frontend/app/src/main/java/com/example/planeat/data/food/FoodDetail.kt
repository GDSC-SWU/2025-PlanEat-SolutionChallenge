package com.example.planeat.data.food

data class FoodDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val category: String,
    val cost: Double,
    val costUnit: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val weightPerServing: String
)