package com.mosalab.submissionpaai.Maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.DataStory
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun MapsScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val token = remember { mutableStateOf<String?>(null) }
    val stories = remember { mutableStateListOf<DataStory>() }

    LaunchedEffect(Unit) {
        token.value = PreferencesManager(context).token.firstOrNull()
        token.value?.let { tokenValue ->
            try {
                val response = ApiService.api.getStoriesWithLocation(
                    token = "Bearer $tokenValue",
                    location = 1
                )
                if (response.isSuccessful) {
                    response.body()?.listStory?.let { storyList ->
                        stories.clear()
                        stories.addAll(storyList)
                    }
                } else {
                    println("API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Error fetching stories: ${e.message}")
            }
        } ?: println("Token is null")
    }


    val initialPosition = remember(stories) {
        stories.firstOrNull()?.let {
            LatLng(it.lat ?: 0.0, it.lon ?: 0.0)
        } ?: LatLng(0.0, 0.0) // Default position
    }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    Box(modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            stories.forEach { story ->
                val lat = story.lat
                val lon = story.lon
                if (lat != null && lon != null) {
                    Marker(
                        state = rememberMarkerState(position = LatLng(lat, lon)),
                        title = story.name,
                        snippet = story.description
                    )
                }
            }
        }
    }
}
