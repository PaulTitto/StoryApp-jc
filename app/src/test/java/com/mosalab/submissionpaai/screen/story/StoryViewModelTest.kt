import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.mosalab.submissionpaai.api.DicodingApiService
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.data.StoriesResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVhJU0gyZE1OSElUb0pkLWYiLCJpYXQiOjE3MzQ5NjM3NjZ9.GCsu9ef9vUQZqys5Sx7NlPHN-9NnYouacQOS9sTxI88"
    private val mockApiService = mockk<DicodingApiService>()
    private val testDispatcher = StandardTestDispatcher()

    private val mockStories = listOf(
        DataStory(
            id = "1",
            name = "Story 1",
            description = "Description 1",
            photoUrl = "photo1.url",
            createdAt = "2023-12-21T10:00:00Z",
            lat = 37.7749,
            lon = -122.4194
        ),
        DataStory(
            id = "2",
            name = "Story 2",
            description = "Description 2",
            photoUrl = "photo2.url",
            createdAt = "2023-12-20T15:30:00Z",
            lat = 34.0522,
            lon = -118.2437
        ),
        DataStory(
            id = "3",
            name = "Story 3",
            description = "Description 3",
            photoUrl = "photo3.url",
            createdAt = "2023-12-19T20:45:00Z",
            lat = 40.7128,
            lon = -74.0060
        )
    )

    private val mockResponse = StoriesResponse(
        listStory = mockStories,
        error = false,
        message = "Success"
    )

    @Test
    fun `test successful story loading - data is not null`() = runTest(testDispatcher) {
        // Mock API response
        coEvery {
            mockApiService.getStories(
                token = eq("Bearer $fakeToken"),
                page = eq(1),
                size = eq(10),
                location = eq(0)
            )
        } returns Response.success(mockResponse)

        // Test PagingSource
        val pagingSource = StoryPagingSource(mockApiService, fakeToken)
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1, // Ensure this matches the expected page
                loadSize = 10,
                placeholdersEnabled = false
            )
        )
        println("LoadResult: $loadResult") // Debugging

        // Assertions
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        val page = loadResult as PagingSource.LoadResult.Page
        assertNotNull(page.data)
        assertEquals(mockStories.size, page.data.size)
        assertEquals(mockStories[0], page.data[0])
    }

    @Test
    fun `test no data returns empty list`() = runTest(testDispatcher) {
//        val correctToken =

        // Correctly mock the API response with matching parameters
        coEvery {
            mockApiService.getStories(
                token = eq("Bearer $fakeToken"),
                page = eq(1),
                size = eq(10),
                location = eq(0)
            )
        } returns Response.success(
            StoriesResponse(
                listStory = emptyList(),
                error = false,
                message = "Success"
            )
        )

        // Test PagingSource
        val pagingSource = StoryPagingSource(mockApiService, fakeToken)
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        println("LoadResult: $loadResult") // Debugging

        // Assertions
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        val page = loadResult as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }

}
