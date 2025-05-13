package com.example.planeat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.planeat.GoogleSearchClient
import com.example.planeat.R
import com.example.planeat.RecommendationClient
import com.example.planeat.RetrofitClient
import com.example.planeat.data.model.HistoryPostRequest
import com.example.planeat.data.model.MealRecommendationResponse
import com.example.planeat.data.model.MealRequest
import com.example.planeat.data.model.UserProfileResponse
import com.example.planeat.data.remote.dto.GoogleSearchResponse
import kotlinx.coroutines.launch
import retrofit2.*

class MealDetailActivity : AppCompatActivity() {

    private lateinit var kcalTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var carbsCountTextView: TextView
    private lateinit var proteinCountTextView: TextView
    private lateinit var fatCountTextView: TextView
    private lateinit var mealImageView: ImageView
    private lateinit var btnReason: Button
    private lateinit var backButton: ImageButton

    private var recommendedLetter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)

        // Initialize UI components
        kcalTextView = findViewById(R.id.tv_kcal)
        titleTextView = findViewById(R.id.mainMealTitle)
        carbsCountTextView = findViewById(R.id.carbsCount)
        proteinCountTextView = findViewById(R.id.proteinCount)
        fatCountTextView = findViewById(R.id.fatCount)
        mealImageView = findViewById(R.id.mainMealImage)
        btnReason = findViewById(R.id.btn_reason)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        btnReason.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("letter", recommendedLetter)
            startActivity(intent)
        }

        // Retrieve ingredients from intent and filter non-empty ones
        val ingredients = listOfNotNull(
            intent.getStringExtra("ingredient1"),
            intent.getStringExtra("ingredient2"),
            intent.getStringExtra("ingredient3")
        ).filter { it.isNotBlank() }

        Log.d("MealDetailActivity", "ingredients -> $ingredients")

        fetchUserProfileFromServer(ingredients.toTypedArray())
    }

    // Get JWT token from SharedPreferences
    private fun getJwtFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val jwt = sharedPreferences.getString("JWT_TOKEN", null)

        if (jwt == null) {
            Log.d("MealDetailActivity", "JWT Token is missing in SharedPreferences")
        } else {
            Log.d("MealDetailActivity", "JWT Token retrieved: $jwt")
        }

        return jwt
    }

    // Get user_email from SharedPreferences
    private fun getEmailFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("USER_EMAIL", null)
    }

    // Fetch user profile from server
    private fun fetchUserProfileFromServer(ingredients: Array<String>) {
        val jwt = getJwtFromSharedPreferences()
        if (jwt.isNullOrEmpty()) {
            Toast.makeText(this, "JWT token is missing", Toast.LENGTH_SHORT).show()
            Log.e("MealDetailActivity", "JWT token is missing")
            return
        }
        val authHeader = "Bearer $jwt"

        val email = getEmailFromSharedPreferences() ?: ""

        lifecycleScope.launch {
            try {
                // Fetch User Profile from server
                val response: Response<UserProfileResponse> = RetrofitClient.apiService.fetchUserProfile(authHeader)

                if (response.isSuccessful) {
                    val userProfileResponse = response.body()

                    if (userProfileResponse != null && userProfileResponse.success) {
                        val userProfile = userProfileResponse.data

                        // Prepare meal request using the fetched user profile
                        val request = MealRequest(
                            user_email = email,
                            age = userProfile.age,
                            gender = parseGender(userProfile.gender),
                            height = userProfile.height.toDouble(),
                            weight = userProfile.weight.toDouble(),
                            can_cook = if (userProfile.canCook) 1 else 0,
                            meals_per_day = userProfile.mealsPerDay,
                            hunger_cycle_min = userProfile.hungerCycle,
                            allergies = userProfile.intolerances?.map { it.lowercase() } ?.joinToString(",") ?: "",
                            available_ingredients = ingredients
                        )

                        Log.d("MealRequest", "Request: $request")

                        // Call meal recommendation API
                        makeApiCall(request)
                    } else {
                        throw Exception("Failed to fetch valid user profile data")
                    }
                } else {
                    throw Exception("Failed to fetch user profile: ${response.code()} ${response.message()}")
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MealDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MealDetailActivity", "Error fetching user profile: ${e.message}")
            }
        }
    }

    // Convert gender string to integer
    private fun parseGender(gender: String?): Int {
        return when (gender?.lowercase()) {
            "male" -> 0
            "female" -> 1
            else ->  1
        }
    }

    // Call the meal recommendation API
    private fun makeApiCall(request: MealRequest) {
        RecommendationClient.apiService.getMealRecommendation(request).enqueue(object : Callback<MealRecommendationResponse> {
            override fun onResponse(call: Call<MealRecommendationResponse>, response: Response<MealRecommendationResponse>) {
                if (response.isSuccessful) {
                    val meal = response.body()!!
                    kcalTextView.text = "${meal.calories} kcal"
                    titleTextView.text = meal.label
                    carbsCountTextView.text = meal.carbs.toString()
                    proteinCountTextView.text = meal.protein.toString()
                    fatCountTextView.text = meal.fat.toString()
                    recommendedLetter = meal.letter

                    getFoodIdAndLoadImage(meal.label)
                } else {
                    Toast.makeText(this@MealDetailActivity, "Recommendation failed", Toast.LENGTH_SHORT).show()
                    Log.e("MealDetailActivity", "API call failed: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MealRecommendationResponse>, t: Throwable) {
                Toast.makeText(this@MealDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Search and load food image using Google Custom Search API
    private fun getFoodIdAndLoadImage(foodName: String) {
        val apiKey = getString(R.string.food_search_api)
        val cseId = getString(R.string.food_cse_id)

        // Step 1: Search for image using Google Custom Search API
        GoogleSearchClient.apiService.searchImage(apiKey, cseId, foodName, "image").enqueue(object : Callback<GoogleSearchResponse> {
            override fun onResponse(call: Call<GoogleSearchResponse>, response: Response<GoogleSearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.items.isNotEmpty()) {
                        // Get the first image URL
                        val imageUrl = searchResponse.items[0].link
                        Log.d("MealDetailActivity", "Found image URL: $imageUrl")

                        // Step 2: Load image using Glide
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this@MealDetailActivity)
                                .load(imageUrl)
                                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                                    override fun onLoadFailed(
                                        e: com.bumptech.glide.load.engine.GlideException?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Log.e("Glide", "Image load failed. Error: ${e?.message}")
                                        if (e != null) {
                                            e.logRootCauses("Glide Error")
                                        }
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: android.graphics.drawable.Drawable?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                        dataSource: com.bumptech.glide.load.DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Log.d("Glide", "Image loaded successfully")

                                        val jwt = getJwtFromSharedPreferences()
                                        if (jwt != null) {
                                            postMealHistory(
                                                jwt = jwt,
                                                foodName = foodName,
                                                foodImgUrl = imageUrl,
                                                recommendReason = recommendedLetter.toString(),
                                                totalCalories = kcalTextView.text.toString().replace("kcal", "").trim().toDouble(),
                                                totalCarbs = carbsCountTextView.text.toString().toDouble(),
                                                totalProtein = proteinCountTextView.text.toString().toDouble(),
                                                totalFat = fatCountTextView.text.toString().toDouble()
                                            )
                                        }

                                        return false
                                    }
                                })
                                .into(mealImageView)  // Load image into ImageView
                        } else {
                            Log.w("MealDetailActivity", "Image URL is null or empty")
                        }
                    } else {
                        Log.w("MealDetailActivity", "No image results found for food: $foodName")
                    }
                } else {
                    Log.e("MealDetailActivity", "Google Custom Search API error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GoogleSearchResponse>, t: Throwable) {
                Log.e("MealDetailActivity", "Image search request failed", t)
            }
        })
    }

    private fun postMealHistory(
        jwt: String,
        foodName: String,
        foodImgUrl: String,
        recommendReason: String,
        totalCalories: Double,
        totalCarbs: Double,
        totalProtein: Double,
        totalFat: Double
    ) {
        val request = HistoryPostRequest(
            foodName = foodName,
            foodImgUrl = foodImgUrl,
            recommendReason = recommendReason,
            totalCalories = totalCalories,
            totalCarbs = totalCarbs,
            totalProtein = totalProtein,
            totalFat = totalFat
        )

        Log.d("MealDetailActivity", "Sending HistoryPostRequest: $request")

        RetrofitClient.apiService.postMealHistory("Bearer $jwt", request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("MealDetailActivity", "Meal history saved successfully")
                    } else {
                        Log.e("MealDetailActivity", "Failed to save meal history: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("MealDetailActivity", "Error saving meal history", t)
                }
            })
    }
}