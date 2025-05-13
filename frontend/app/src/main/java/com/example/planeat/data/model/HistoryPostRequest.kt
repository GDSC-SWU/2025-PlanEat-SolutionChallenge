package com.example.planeat.data.model

data class HistoryPostRequest(
    val foodName: String,
    val foodImgUrl: String,
    val recommendReason: String,
    val totalCalories: Double,
    val totalCarbs: Double,
    val totalProtein: Double,
    val totalFat: Double
)