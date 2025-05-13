package com.example.planeat.network.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object PlaneatApi {

    private const val API_URL = "https://us-central1-planeat-gemma.cloudfunctions.net/gemini-chat-final"

    suspend fun generateResponse(userEmail: String, userMessage: String): String {
        val client = OkHttpClient()

        // Build request JSON with user_email and user_message
        val json = """
            {
              "user_email": "$userEmail",
              "user_message": "$userMessage"
            }
        """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(API_URL)
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@use "Error: ${response.code} - ${response.message}"
                    }

                    val resBody = response.body?.string()
                    if (resBody.isNullOrEmpty()) {
                        return@use "No content returned from API."
                    }

                    val obj = JSONObject(resBody)
                    return@use obj.optString("bot_reply", "No response text found.")
                }
            } catch (e: Exception) {
                return@withContext "Exception occurred: ${e.localizedMessage}"
            }
        }
    }
}