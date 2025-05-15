package com.example.planeat.network.api

import com.example.planeat.data.remote.dto.GoogleSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleSearchApi {
    @GET("v1")
    fun searchImage(
        @Query("key") apiKey: String,
        @Query("cx") cseId: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = "image"
    ): Call<GoogleSearchResponse>
}
