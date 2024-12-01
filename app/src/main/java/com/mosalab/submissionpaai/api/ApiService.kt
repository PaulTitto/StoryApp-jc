package com.mosalab.submissionpaai.api

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.data.LoginRequest
import com.mosalab.submissionpaai.data.LoginResponse
import com.mosalab.submissionpaai.data.RegisterRequest
import com.mosalab.submissionpaai.data.RegisterResponse
import com.mosalab.submissionpaai.data.StoriesResponse
import com.mosalab.submissionpaai.data.StoryDetailResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DicodingApiService {
    @POST("/v1/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/v1/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("/v1/stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0
    ): Response<StoriesResponse>

    @GET("/stories/{id}")
    suspend fun getStoryById(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Response<StoryDetailResponse>
}

object ApiService {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: DicodingApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DicodingApiService::class.java)
    }

    var stories = mutableStateListOf<DataStory>()
        private set

    var isLoading = mutableStateOf(false)
}
