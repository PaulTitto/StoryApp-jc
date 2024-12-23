import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mosalab.submissionpaai.api.DicodingApiService
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.data.StoriesResponse

class StoryPagingSource(
    private val apiService: DicodingApiService,
    private val token: String
) : PagingSource<Int, DataStory>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataStory> {
        val page = params.key ?: 1
        return try {
            val response = apiService.getStories("Bearer $token", page, params.loadSize)
            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to load stories"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, DataStory>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}
