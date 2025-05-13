package com.example.planeat.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import com.example.planeat.data.UserProfileManager
import com.example.planeat.data.model.UpdateUserProfile
import com.example.planeat.data.model.UserProfileResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var ageInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var fromInput: EditText
    private lateinit var saveButton: Button
    private lateinit var editButton: Button
    private lateinit var allergiesGrid: GridLayout
    private lateinit var backButton: ImageButton

    // Used to preserve unedited gender and cooking ability
    private var originalGender: String? = null
    private var originalCanCook: Boolean? = null
    private var originalMealsPerDay: Int? = null
    private var originalHungerCycle: Int? = null

    private var isEditing = false  // Indicates edit mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // View Binding
        tvUserName    = findViewById(R.id.tv_user_name)
        ageInput      = findViewById(R.id.age_input)
        heightInput   = findViewById(R.id.height_input)
        weightInput   = findViewById(R.id.weight_input)
        fromInput     = findViewById(R.id.from_input)
        saveButton    = findViewById(R.id.save_button)
        editButton    = findViewById(R.id.edit_button)
        allergiesGrid = findViewById(R.id.allergies_grid)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener { finish() }

        UserProfileManager.init(this)

        disableEditing()            // Start in view-only mode
        fetchUserProfileFromServer()

        editButton.setOnClickListener { enableEditing() }
        saveButton.setOnClickListener { saveUserProfile() }
    }

    private fun getJwtFromSharedPreferences(): String? {
        val sp = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        return sp.getString("JWT_TOKEN", null)
    }

    private fun enableEditing() {
        isEditing = true
        ageInput.isEnabled      = true
        heightInput.isEnabled   = true
        weightInput.isEnabled   = true
        fromInput.isEnabled     = true
        allergiesGrid.isEnabled = true
        editButton.visibility   = View.GONE
        saveButton.visibility   = View.VISIBLE
    }

    private fun disableEditing() {
        isEditing = false
        ageInput.isEnabled      = false
        heightInput.isEnabled   = false
        weightInput.isEnabled   = false
        fromInput.isEnabled     = false
        allergiesGrid.isEnabled = false
        editButton.visibility   = View.VISIBLE
        saveButton.visibility   = View.GONE
    }

    private fun handleAllergyButtons(intolerances: List<String>?) {
        // null check: if intolerances is null, use an empty list
        val safeIntolerances = intolerances ?: emptyList()

        allergiesGrid.children.forEach { view ->
            val btn = view as Button

            // Toggle button selection only in edit mode
            btn.setOnClickListener {
                if (isEditing) {
                    if (btn.isSelected) {
                        btn.isSelected = false
                        btn.setBackgroundResource(R.drawable.button_default)
                    } else {
                        btn.isSelected = true
                        btn.setBackgroundResource(R.drawable.button_selected)
                    }
                }
            }

            // Set initial selection state
            if (safeIntolerances.contains(btn.text.toString())) {
                btn.isSelected = true
                btn.setBackgroundResource(R.drawable.button_selected)
            } else {
                btn.isSelected = false
                btn.setBackgroundResource(R.drawable.button_default)
            }
        }
    }

    // Fetch user profile from server using Retrofit
    private fun fetchUserProfileFromServer() {
        val jwt = getJwtFromSharedPreferences()
        if (jwt.isNullOrEmpty()) {
            Toast.makeText(this, "JWT token is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val authHeader = "Bearer $jwt"
        Log.d("MyPageActivity", "Fetching profile with token: $jwt")

        lifecycleScope.launch {
            try {
                val response: Response<UserProfileResponse> = RetrofitClient.apiService.fetchUserProfile(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val userProfileResponse = response.body()!!
                    val userProfile = userProfileResponse.data

                    // Log userProfile for debugging
                    Log.d("MyPageActivity", "UserProfile: $userProfile")

                    originalGender = userProfile.gender
                    originalCanCook = userProfile.canCook
                    originalMealsPerDay = userProfile.mealsPerDay
                    originalHungerCycle = userProfile.hungerCycle

                    // Set data to UI elements
                    tvUserName.text = userProfile.name
                    ageInput.setText(if (userProfile.age > 0) userProfile.age.toString() else "")
                    heightInput.setText(if (userProfile.height > 0) userProfile.height.toString() else "")
                    weightInput.setText(if (userProfile.weight > 0) userProfile.weight.toString() else "")
                    fromInput.setText(userProfile.location)
                    handleAllergyButtons(userProfile.intolerances)
                } else {
                    Log.d("MyPageActivity", "Failed to fetch profile: ${response.code()}")
                    Toast.makeText(this@MyPageActivity, "Failed to fetch profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MyPageActivity", "Error loading profile", e)
                Toast.makeText(this@MyPageActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save user profile to server using Retrofit
    private fun saveUserProfile() {
        val jwt = getJwtFromSharedPreferences()
        if (jwt.isNullOrEmpty()) {
            Toast.makeText(this, "JWT token is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val authHeader = "Bearer $jwt"

        // Create UpdateUserProfile with the updated values
        val updateUserProfile = UpdateUserProfile(
            name = tvUserName.text.toString(),
            gender = originalGender ?: "female",
            age = ageInput.text.toString().toIntOrNull() ?: 0,
            height = heightInput.text.toString().toIntOrNull() ?: 0,
            weight = weightInput.text.toString().toIntOrNull() ?: 0,
            location = fromInput.text.toString(),
            mealsPerDay = originalMealsPerDay ?: 0,
            hungerCycle = originalHungerCycle ?: 0,
            canCook = originalCanCook ?: false,
            intolerances = allergiesGrid.children
                .mapNotNull { view ->
                    val btn = view as Button
                    if (btn.isSelected) btn.text.toString() else null
                }.toList()
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateUserProfile(authHeader, updateUserProfile)
                if (response.isSuccessful) {
                    Toast.makeText(this@MyPageActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                    disableEditing()
                } else {
                    Toast.makeText(this@MyPageActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MyPageActivity", "Error updating profile", e)
                Toast.makeText(this@MyPageActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}