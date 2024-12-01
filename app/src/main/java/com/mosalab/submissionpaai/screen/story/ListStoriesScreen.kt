package com.mosalab.submissionpaai.screen.story

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.DataStory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun ListStoriesScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val stories = remember { mutableStateListOf<DataStory>() }
    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }
    val token = remember { mutableStateOf<String?>(null) }
    val page = remember { mutableStateOf(1) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        token.value = PreferencesManager(context).token.firstOrNull()
    }

    LaunchedEffect(token.value) {
        token.value?.let { authToken ->
            if (stories.isEmpty() && !isLoading.value) {
                isLoading.value = true
                try {
                    val response = ApiService.api.getStories("Bearer $authToken", page = page.value, size = 10)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && !body.error) {
                            stories.addAll(body.listStory)
                        } else {
                            isError.value = true
                            showToast(context, "Error: ${body?.message ?: "Unknown error"}")
                        }
                    } else {
                        isError.value = true
                        showToast(context, "Error: ${response.message()}")
                    }
                } catch (e: Exception) {
                    isError.value = true
                    showToast(context, "Error fetching stories: ${e.message}")
                } finally {
                    isLoading.value = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
//        LogoutButton(navController = navController)

        when {
            isLoading.value -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            isError.value -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Something went wrong. Please try again later.", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn {
                    items(stories) { story ->
                        StoryListItem(
                            story = story,
                            onClick = {
                                navController.navigate("detail/${story.id}")
                            }
                        )
                    }

                    item {
                        if (!isLoading.value) {
                            LaunchedEffect(Unit) {
                                page.value += 1
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutButton(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                PreferencesManager(context).clearSession()
                navController.navigate("login")
            }
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("Logout")
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun StoryListItem(story: DataStory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(story.photoUri),
                contentDescription = "Story image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListStoriesScreenPreview() {
    val mockStories = listOf(
        DataStory(
            id = 1.toString(),
            name = "Story 1",
            description = "This is the first story",
            photoUri = "https://via.placeholder.com/150",
            createdAt = "",
            lat = null,
            lon = null
        ),)

    val navController = rememberNavController()

    ListStoriesScreen(navController = navController)
}
