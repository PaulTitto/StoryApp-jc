package com.mosalab.submissionpaai.screen.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.StoryDetail
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun DetailListStoryScreen(
    navController: NavController,
    storyId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val storyDetail = remember { mutableStateOf<StoryDetail?>(null) }
    val isError = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading.value = true
        try {
            val token = PreferencesManager(context).token.firstOrNull()
            if (token != null) {
                val response = ApiService.api.getStoryById("Bearer $token", storyId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error) {
                        storyDetail.value = body.story
                    } else {
                        isError.value = true
                        showToast(context, body?.message ?: "Error fetching details.")
                    }
                } else {
                    isError.value = true
                    showToast(context, "Error: ${response.message()}")
                }
            }
        } catch (e: Exception) {
            isError.value = true
            showToast(context, "Error: ${e.message}")
        } finally {
            isLoading.value = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading.value -> CircularProgressIndicator()
            isError.value -> Text(
                "Failed to load story details.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            storyDetail.value != null -> {
                val story = storyDetail.value!!
                AsyncImage(
                    model = story.photoUrl,
                    contentDescription = "Story Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.name,
                    fontSize = 24.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story.description,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back to List")
                }
            }
        }
    }
}



