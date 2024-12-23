package com.mosalab.submissionpaai.data

data class DataStory(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double,
    val lon: Double
) {
    companion object {
        val DIFF_CALLBACK = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<DataStory>() {
            override fun areItemsTheSame(oldItem: DataStory, newItem: DataStory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataStory, newItem: DataStory): Boolean {
                return oldItem == newItem
            }
        }
    }
}


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