package com.mosalab.submissionpaai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.data.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class StoryViewModel(private val token: String) : ViewModel() {

    val storyPagingData: Flow<PagingData<DataStory>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { StoryPagingSource(ApiService, token) }
    ).flow
}
