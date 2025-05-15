package com.example.planeat.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.suspendCancellableCoroutine

data class UserProfileData(
    var idToken: String? = null,
    var name: String? = null,
    var gender: String? = null,
    var age: Int? = null,
    var height: Int? = null,
    var weight: Int? = null,
    var location: String? = null,
    var mealsPerDay: Int? = null,
    var hungerCycle: Int? = null,
    var canCook: Boolean? = null,
    var intolerances: List<String>? = null
)

object UserProfileManager {

    // SharedPreferences for saving user data
    private lateinit var sharedPreferences: SharedPreferences

    // UserProfileData object to hold user data
    val userData = UserProfileData()

    // Initialization function, requires Context
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
    }

    fun fetchFreshIdToken(callback: (String?) -> Unit) {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Fetch fresh ID token with forceRefresh
            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    val token = result.token
                    saveIdToken(token)  // Save token to cache
                    callback(token)
                }
                .addOnFailureListener {
                    callback(null)  // Return null in case of failure
                }
        } else {
            callback(null)  // Return null if no user is logged in
        }
    }

    // Function to save ID token in SharedPreferences
    fun saveIdToken(idToken: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("ID_TOKEN", idToken)
        editor.apply()

        // Reflect the saved token in the userData object
        userData.idToken = idToken
    }

    // UserProfileManager.kt
    suspend fun getFreshIdTokenSuspend(): String? = suspendCancellableCoroutine { cont ->
        fetchFreshIdToken { token ->
            cont.resume(token, null)
        }
    }
}
