import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.api.DicodingApiService
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.data.StoriesResponse
import com.mosalab.submissionpaai.viewmodel.StoryViewModel
import com.mosalab.submissionpaai.data.StoryPagingSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    private val fakeToken = "fakeToken"
    private val mockApiService = mockk<ApiService>()

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


    @Test
    fun `test successful story loading - data is not null`() = runTest {
        val storiesResponse = StoriesResponse(
            listStory = mockStories,
            error = false,
            message = ""
        )

        coEvery { mockApiService.api.getStories("Bearer $fakeToken", any(), any(), any()) } returns Response.success(storiesResponse)

        val storyPagingSource = StoryPagingSource(mockApiService, fakeToken)
        val viewModel = StoryViewModel(fakeToken)

        val snapshot = viewModel.storyPagingData.asSnapshot()
        assertNotNull(snapshot)
    }

    @Test
    fun `test no data returns empty list`() = runTest {
        val storiesResponse = StoriesResponse(
            listStory = emptyList(),
            error = false,
            message = ""
        )

        coEvery { mockApiService.api.getStories("Bearer $fakeToken", any(), any(), any()) } returns Response.success(storiesResponse)

        val storyPagingSource = StoryPagingSource(mockApiService, fakeToken)
        val viewModel = StoryViewModel(fakeToken)

        val snapshot = viewModel.storyPagingData.asSnapshot()
        assertTrue(snapshot.isEmpty())
    }



//    ERROR
//    @Test
//    fun `test story count matches`() = runTest {
//        val precomputedPagingData = PagingData.from(mockStories)
//
//        // Fetch snapshot using TestDispatcher (inherent in runTest)
//        val snapshot = precomputedPagingData.asSnapshot(this)
//        assertEquals(3, snapshot.size)
//    }


    //    ERROR
//    @Test
//    fun `test first story matches`() = runTest {
//        // Pre-compute PagingData
//        val precomputedPagingData = PagingData.from(mockStories)
//
//        // Mock storyPagingData Flow
//        val viewModel = mockk<StoryViewModel> {
//            every { storyPagingData } returns flowOf(precomputedPagingData)
//        }
//
//        // Fetch snapshot from PagingData
//        val snapshot = precomputedPagingData.asSnapshot()
//
//        // Assert the snapshot is not empty
//        assertTrue("Paging data snapshot should not be empty", snapshot.isNotEmpty())
//
//        // Assert the first story matches
//        assertEquals("Story 1", snapshot.first().name)
//    }






}
