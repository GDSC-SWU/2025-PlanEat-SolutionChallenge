package com.example.planeat.data.model

// Structure that wraps the response
data class UserProfileResponse(
    val success: Boolean,
    val message: String,
    val data: UserProfile
)

// UserProfile model
data class UserProfile(
    val id: Int,
    val email: String?,
    val name: String?,
    val gender: String?,
    val age: Int,
    val height: Int,
    val weight: Int,
    val location: String?,
    val joinedAt: String?,
    val updatedAt: String?,
    val mealsPerDay: Int,
    val hungerCycle: Int,
    val canCook: Boolean,
    val intolerances: List<String>?
)