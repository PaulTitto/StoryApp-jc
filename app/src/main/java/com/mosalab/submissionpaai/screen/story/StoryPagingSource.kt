package com.mosalab.submissionpaai.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mosalab.submissionpaai.api.ApiService

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, DataStory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataStory> {
        val page = params.key ?: 1
        return try {
            val response = apiService.api.getStories("Bearer $token", page = page, size = 10)
            val stories = response.body()?.listStory ?: emptyList()

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DataStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
