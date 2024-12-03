package com.mosalab.submissionpaai.data

data class DataStory(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl:  String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: StoryDetail
)

data class StoryDetail(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double?,
    val lon: Double?
)

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<DataStory>
)


data class AddStoryRequest(
    val description: String,
    val photoUrl: String,
    val lat: Double?,
    val lon: Double?
)
data class AddStoryResponse(
    val error: Boolean,
    val message: String
)