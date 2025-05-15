package com.example.planeat.network.api

import com.example.planeat.data.model.HistoryPostRequest
import com.example.planeat.data.model.MealRecommendationResponse
import com.example.planeat.data.model.MealRequest
import com.example.planeat.data.model.UpdateUserProfile
import com.example.planeat.data.remote.dto.FoodResponse
import com.example.planeat.data.remote.dto.SignupRequest
import com.example.planeat.data.remote.dto.HomeResponse
import com.example.planeat.data.remote.dto.LoginRequest
import com.example.planeat.data.remote.dto.LoginResponse
import com.example.planeat.data.model.UserProfileResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    // signup
    @POST("/api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<Void>

    // login
    @POST("/api/auth/login")
    suspend fun loginWithGoogle(@Body request: LoginRequest): Response<LoginResponse>

    // home
    @GET("/api/home")
    suspend fun getHomeData(@Header("Authorization") authHeader: String): Response<HomeResponse>

    // User Info - Fetch
    @GET("api/users/info")
    suspend fun fetchUserProfile(@Header("Authorization") authHeader: String): Response<UserProfileResponse>

    // User Info - Update
    @PUT("api/users/info")
    suspend fun updateUserProfile(@Header("Authorization") authHeader: String, @Body updateUserProfile: UpdateUserProfile): Response<Unit>

    // Meal Recommendation
    @POST("recommend")
    fun getMealRecommendation(@Body request: MealRequest): Call<MealRecommendationResponse>

    // Food Search (by keyword)
    @GET("/api/foods")
    suspend fun searchFoods(@Query("keyword") keyword: String, @Query("size") size: Int = 10): Response<FoodResponse>

    // save histories
    @POST("/api/histories")
    fun postMealHistory(@Header("Authorization") token: String, @Body request: HistoryPostRequest): Call<Void>
}