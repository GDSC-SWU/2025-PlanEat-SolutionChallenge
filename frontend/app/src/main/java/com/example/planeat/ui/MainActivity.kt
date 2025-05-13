package com.example.planeat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvName: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvKcalValue: TextView
    private lateinit var tvCarbsCount: TextView
    private lateinit var tvProteinCount: TextView
    private lateinit var tvFatCount: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var askToChatButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views by ID
        tvGreeting = findViewById(R.id.tv_greeting)
        tvName = findViewById(R.id.tv_name)
        tvDate = findViewById(R.id.tv_date)
        tvKcalValue = findViewById(R.id.tv_kcal_value)
        tvCarbsCount = findViewById(R.id.carbsCount)
        tvProteinCount = findViewById(R.id.proteinCount)
        tvFatCount = findViewById(R.id.fatCount)
        ivAvatar = findViewById(R.id.iv_avatar)
        askToChatButton = findViewById(R.id.askToChatButton)

        // Set current date dynamically
        val currentDate = getCurrentDate()
        tvDate.text = currentDate

        // Navigate to MyPageActivity when avatar is clicked
        ivAvatar.setOnClickListener {
            navigateToMyPage()
        }

        val cardMakeDinner: CardView = findViewById(R.id.btn_make_dinner)

        // Set click listener on the card view
        cardMakeDinner.setOnClickListener {
            val intent = Intent(this, MealActivity::class.java)
            startActivity(intent)
        }

        askToChatButton.setOnClickListener {
            // Get email from SharedPreferences
            val email = getEmailFromSharedPreferences()

            // Start ChatActivity when the button is clicked
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            intent.putExtra("user_email", email)
            startActivity(intent)
        }

        // Call /api/home API
        fetchHomeData()
    }

    override fun onResume() {
        super.onResume()
        fetchHomeData()
    }

    // Method to get the email from SharedPreferences
    private fun getEmailFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("USER_EMAIL", null)  // Retrieve email using the key "USER_EMAIL"
    }

    // Get the current date
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }

    // Navigate to MyPageActivity
    private fun navigateToMyPage() {
        val intent = Intent(this, MyPageActivity::class.java)
        startActivity(intent)
    }

    // Get JWT from SharedPreferences
    private fun getJwtFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val jwt = sharedPreferences.getString("JWT_TOKEN", null) // "JWT_TOKEN" 키로 저장된 토큰 가져오기

        if (jwt == null) {
            Log.d("MainActivity", "JWT Token is missing in SharedPreferences")
        } else {
            Log.d("MainActivity", "JWT Token retrieved: $jwt")
        }

        return jwt
    }

    // Fetch home data by calling the /api/home API
    private fun fetchHomeData() {
        val jwt = getJwtFromSharedPreferences()
        if (jwt == null) {
            Toast.makeText(this, "JWT token is missing", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "JWT token is missing")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authHeader = "Bearer $jwt"
                val response = RetrofitClient.apiService.getHomeData(authHeader)

                if (response.isSuccessful) {
                    val homeResponse = response.body()
                    if (homeResponse?.success == true && homeResponse.data != null) {
                        val userName = homeResponse.data.userName
                        val kcal = homeResponse.data.todayTotalCal
                        val carbs = homeResponse.data.todayTotalCarb
                        val protein = homeResponse.data.todayTotalProtein
                        val fat = homeResponse.data.todayTotalFat

                        runOnUiThread {
                            tvName.text = "$userName!"
                            tvKcalValue.text = String.format("%.0f", kcal)
                            tvCarbsCount.text = String.format("%.1f", carbs)
                            tvProteinCount.text = String.format("%.1f", protein)
                            tvFatCount.text = String.format("%.1f", fat)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "API Call Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("MainActivity", "API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: HttpException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "HTTP Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity", "HTTP Exception: ${e.message}")
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity", "Network Error: ${e.localizedMessage}", e)
            }
        }
    }
}
