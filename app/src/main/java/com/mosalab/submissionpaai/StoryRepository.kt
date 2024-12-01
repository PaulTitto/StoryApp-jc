package com.mosalab.submissionpaai

import android.util.Log
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.Request

class StoryRepository(private val preferencesManager: PreferencesManager) {

    private val client = OkHttpClient()

    suspend fun fetchStories() {
        val token = preferencesManager.token.first()  // Fetch the token synchronously
        if (token != null) {
            // Make the API request using the token
            val request = Request.Builder()
                .url("https://story-api.dicoding.dev/v1/stories?page=1&size=10&location=0")
                .addHeader("Authorization", "Bearer $token")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Handle successful response
                } else {
                    // Handle failure (e.g., 401 Unauthorized)
                    Log.e("StoryRepository", "Request failed with code: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("StoryRepository", "Request failed: ${e.message}")
            }
        } else {
            // Handle the case where the token is missing or null
            Log.e("StoryRepository", "Token is null or invalid")
        }
    }
}
