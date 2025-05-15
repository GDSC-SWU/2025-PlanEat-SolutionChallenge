package com.example.planeat.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import com.example.planeat.data.UserProfileManager
import com.example.planeat.data.remote.dto.LoginRequest
import com.example.planeat.ui.MainActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Step1Fragment())
                .commit()
        }
    }

    fun goToStep(step: Int) {
        val fragment: Fragment = when (step) {
            1 -> Step1Fragment()
            2 -> Step2Fragment()
            3 -> Step3Fragment()
            else -> return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Refresh JWT using ID token and navigate to the main screen
    fun goToHomeWithJwtRefresh() {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            Toast.makeText(this, "Unexpected error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch(errorHandler) {
            val idToken = UserProfileManager.getFreshIdTokenSuspend()
            if (idToken.isNullOrEmpty()) {
                Toast.makeText(this@SignupActivity, "Unable to fetch ID token", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val response = RetrofitClient.apiService.loginWithGoogle(LoginRequest(idToken))
                if (response.isSuccessful && response.body() != null) {
                    val jwt = response.body()!!.data.jwt

                    // Save JWT to SharedPreferences
                    val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    sharedPreferences.edit().putString("JWT_TOKEN", jwt).apply()

                    // Save ID token using UserProfileManager if needed
                    UserProfileManager.saveIdToken(jwt)

                    // Navigate to MainActivity
                    val intent = Intent(this@SignupActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignupActivity, "Failed to refresh JWT", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignupActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}