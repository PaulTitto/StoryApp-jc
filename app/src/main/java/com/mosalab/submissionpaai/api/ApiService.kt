package com.mosalab.submissionpaai.api

import com.mosalab.submissionpaai.data.AddStoryResponse
import com.mosalab.submissionpaai.data.LoginRequest
import com.mosalab.submissionpaai.data.LoginResponse
import com.mosalab.submissionpaai.data.RegisterRequest
import com.mosalab.submissionpaai.data.RegisterResponse
import com.mosalab.submissionpaai.data.StoriesResponse
import com.mosalab.submissionpaai.data.StoryDetailResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("/v1/stories/{id}")
    suspend fun getStoryById(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Response<StoryDetailResponse>

    @Multipart
    @POST("v1/stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<AddStoryResponse>


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
}
