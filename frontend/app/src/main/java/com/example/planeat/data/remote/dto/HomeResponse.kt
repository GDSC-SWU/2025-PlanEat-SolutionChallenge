package com.example.planeat.data.remote.dto

data class HomeResponse(
    val success: Boolean,
    val data: HomeData?
)

data class HomeData(
    val userName: String,
    val todayTotalCal: Double,
    val todayTotalCarb: Double,
    val todayTotalProtein: Double,
    val todayTotalFat: Double
)