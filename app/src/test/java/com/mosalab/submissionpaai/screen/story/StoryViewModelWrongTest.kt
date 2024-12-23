//package com.mosalab.submissionpaai.viewmodel
//
//import androidx.paging.PagingData
//import androidx.recyclerview.widget.ListUpdateCallback
//import com.mosalab.submissionpaai.data.DataStory
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotNull
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.MockitoAnnotations
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//
//@RunWith(RobolectricTestRunner::class)
//@Config(manifest = Config.NONE)
//@OptIn(ExperimentalCoroutinesApi::class)
//class StoryViewModelWrongTest {
//
//    private lateinit var viewModel: StoryViewModel
//
//    private val testDispatcher = StandardTestDispatcher()
//    private val testScope = TestScope(testDispatcher)
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//        Dispatchers.setMain(testDispatcher)
//        viewModel = StoryViewModel("dummy_token")
//    }
//
//    @Test
//    fun `verify storyPagingData emits correct paging data`() = testScope.runTest {
//        val dummyStories = listOf(
//            DataStory("1", "Story 1", "Description 1", "url1", "2022-12-01", null, null),
//            DataStory("2", "Story 2", "Description 2", "url2", "2022-12-02", null, null)
//        )
//        val pagingData = PagingData.from(dummyStories)
//        val pagingFlow = flowOf(pagingData)
//
//        // Collect PagingData into a snapshot using PagingDataDiffer
//        val differ = collectPagingData(pagingFlow)
//
//        // Assert items in snapshot
//        assertNotNull(differ)
//        assertEquals(2, differ.size)
//        assertEquals(dummyStories[0], differ[0])
//    }
//
//    @Test
//    fun `when there are no story data, ensure the number of items is zero`() = testScope.runTest {
//        val emptyStories = listOf<DataStory>()
//        val pagingData = PagingData.from(emptyStories)
//        val pagingFlow = flowOf(pagingData)
//
//        // Collect PagingData into a snapshot using PagingDataDiffer
//        val differ = collectPagingData(pagingFlow)
//
//        // Assert items in snapshot
//        assertNotNull(differ)
//        assertEquals(0, differ.size)
//    }
//
//    /**
//     * Helper function to collect PagingData into a snapshot using PagingDataDiffer
//     */
//    private suspend fun collectPagingData(pagingFlow: kotlinx.coroutines.flow.Flow<PagingData<DataStory>>): List<DataStory> {
//        val differ = androidx.paging.AsyncPagingDataDiffer(
//            diffCallback = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<DataStory>() {
//                override fun areItemsTheSame(oldItem: DataStory, newItem: DataStory): Boolean = oldItem.id == newItem.id
//                override fun areContentsTheSame(oldItem: DataStory, newItem: DataStory): Boolean = oldItem == newItem
//            },
//            updateCallback = object : ListUpdateCallback {
//                override fun onInserted(position: Int, count: Int) {}
//                override fun onRemoved(position: Int, count: Int) {}
//                override fun onMoved(fromPosition: Int, toPosition: Int) {}
//                override fun onChanged(position: Int, count: Int, payload: Any?) {}
//            },
//            mainDispatcher = Dispatchers.Main,
//            workerDispatcher = Dispatchers.IO
//        )
//
//        pagingFlow.collect { pagingData ->
//            differ.submitData(pagingData)
//        }
//
//        // Return the snapshot of the collected items
//        return differ.snapshot().items
//    }
//}
