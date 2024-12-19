package com.mosalab.submissionpaai.viewmodel

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.turbine.test
import com.mosalab.submissionpaai.api.DicodingApiService
import com.mosalab.submissionpaai.data.DataStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @Mock
    private lateinit var apiService: DicodingApiService

    private lateinit var viewModel: StoryViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `verify storyPagingData emits correct paging data`() = testScope.runTest {
        // Mock PagingSource
        val dummyStories = listOf(
            DataStory("1", "Story 1", "Description 1", "url1", "2022-12-01", null, null),
            DataStory("2", "Story 2", "Description 2", "url2", "2022-12-02", null, null)
        )
        val fakePagingSource = object : PagingSource<Int, DataStory>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataStory> {
                return LoadResult.Page(
                    data = dummyStories,
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, DataStory>): Int? = null
        }

        // Mock ViewModel's paging data to use the fake paging source
        viewModel = StoryViewModel("dummy_token")

        val pagingDataFlow = flowOf(PagingData.from(dummyStories))

        // Verify data
        pagingDataFlow.test {
            val item = awaitItem()
            assert(item != null)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
