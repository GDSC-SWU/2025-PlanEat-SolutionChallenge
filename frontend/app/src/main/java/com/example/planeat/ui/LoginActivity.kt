package com.example.planeat.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.planeat.R
import com.example.planeat.RetrofitClient
import com.example.planeat.data.UserProfileManager
import com.example.planeat.data.remote.dto.LoginRequest
import com.example.planeat.ui.signup.SignupActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // Launcher for Google sign-in result
    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                firebaseAuthWithGoogle(idToken)
            } catch (e: Exception) {
                Log.e("Login", "Google sign-in failed: ${e.localizedMessage}")
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UserProfileManager
        UserProfileManager.init(this)
        auth = FirebaseAuth.getInstance()

        // Customize Google login button text style
        val googleLoginButton = findViewById<Button>(R.id.btn_google_login)
        val fullText = "Sign in with Google"
        val spannable = SpannableString(fullText)

        val lightFont = ResourcesCompat.getFont(this, R.font.pretendard_light)
        val boldFont = ResourcesCompat.getFont(this, R.font.pretendard_bold)

        spannable.setSpan(
            CustomTypefaceSpan(lightFont ?: return),
            0,
            "Sign in with ".length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            CustomTypefaceSpan(boldFont ?: return),
            "Sign in with ".length,
            fullText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        googleLoginButton.text = spannable

        // Auto-login if user is already authenticated
        if (auth.currentUser != null) {
            checkIfNewUser()
            return
        }

        // Configure Google sign-in options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Handle login button click
        findViewById<Button>(R.id.btn_google_login).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }

    // Authenticate Firebase with Google ID token
    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken == null) {
            Log.e("Login", "idToken is null")
            Toast.makeText(this, "Google token is invalid.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Login", "Received Google ID Token: $idToken")

        UserProfileManager.saveIdToken(idToken)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val isNewUser = result?.additionalUserInfo?.isNewUser == true

                    Log.d("Login", "Firebase login successful: ${auth.currentUser?.email}, isNewUser: $isNewUser")

                    if (isNewUser) {
                        sendUserInfoToBackend(true)
                    } else {
                        sendUserInfoToBackend(false)
                    }

                } else {
                    Log.e("Login", "Firebase authentication failed", task.exception)
                    Toast.makeText(this, "Firebase login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Send user info to backend server after login
    private fun sendUserInfoToBackend(isNewUser: Boolean) {
        val user = auth.currentUser ?: return

        user.getIdToken(true).addOnCompleteListener { tokenTask ->
            if (tokenTask.isSuccessful) {
                val idToken = tokenTask.result?.token ?: run {
                    Log.e("Backend", "Firebase ID Token is null or empty.")
                    return@addOnCompleteListener
                }

                Log.d("Backend", "Firebase Auth ID Token: $idToken")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = LoginRequest(idToken)
                        val response = RetrofitClient.apiService.loginWithGoogle(request)

                        if (response.isSuccessful && response.body() != null) {
                            val loginData = response.body()!!.data
                            Log.d("Backend", "JWT: ${loginData.jwt}, userId: ${loginData.userId}")
                            saveJwtToSharedPreferences(loginData.jwt)
                            saveEmailToSharedPreferences(user.email)

                            // Navigate to next screen only after JWT has been saved
                            withContext(Dispatchers.Main) {
                                if (isNewUser) {
                                    goToStep1()
                                } else {
                                    goToMain()
                                }
                            }
                        } else {
                            Log.e("Backend", "Login failed: ${response.code()} - ${response.message()}")
                        }
                    } catch (e: Exception) {
                        Log.e("Backend", "Exception: ${e.localizedMessage}")
                    }
                }
            } else {
                Log.e("Backend", "Failed to get Firebase ID Token: ${tokenTask.exception}")
            }
        }
    }

    // Save JWT token locally
    private fun saveJwtToSharedPreferences(jwt: String) {
        Log.d("JWT", "saveJwtToSharedPreferences called")

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("JWT_TOKEN", jwt)
        val success = editor.commit()

        if (success) {
            Log.d("JWT", "JWT saved to SharedPreferences: $jwt")
        } else {
            Log.e("JWT", "Failed to save JWT to SharedPreferences")
        }
    }

    // Save user email to SharedPreferences
    private fun saveEmailToSharedPreferences(email: String?) {
        if (email == null) {
            Log.e("Login", "Email is null, cannot save to SharedPreferences")
            return
        }

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_EMAIL", email)
        val success = editor.commit()

        if (success) {
            Log.d("Login", "Email saved to SharedPreferences: $email")
        } else {
            Log.e("Login", "Failed to save email to SharedPreferences")
        }
    }

    // Determine if user is new or returning
    private fun checkIfNewUser() {
        val user = auth.currentUser ?: return
        if (user.metadata?.creationTimestamp == user.metadata?.lastSignInTimestamp) {
            goToStep1()
        } else {
            goToMain()
        }
    }

    private fun goToStep1() {
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}