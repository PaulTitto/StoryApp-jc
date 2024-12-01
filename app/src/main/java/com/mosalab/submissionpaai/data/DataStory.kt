package com.mosalab.submissionpaai.data

data class DataStory(
    val id: String,
    val name: String,
    val description: String,
    val photoUri:  String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: DataStory
)
data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<DataStory>
)
