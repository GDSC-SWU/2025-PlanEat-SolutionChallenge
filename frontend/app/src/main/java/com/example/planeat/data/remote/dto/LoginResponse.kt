package com.example.planeat.data.remote.dto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val jwt: String,
    val userId: Int
)