package com.mosalab.submissionpaai.screen.story

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
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

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            when {
                isLoading.value -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                isError.value -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Something went wrong. Please try again later.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {

                    LazyColumn(modifier = modifier.background(Color.White)) {
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
        FloatingActionButton(
            onClick = {
                navController.navigate("uploadStory")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Upload Story")
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
            .padding(16.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = story.photoUrl,
            contentDescription = "Story image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = story.name,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = story.description,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )


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
            photoUrl = "https://via.placeholder.com/150",
            createdAt = "",
            lat = null,
            lon = null
        ),
    )

    val navController = rememberNavController()

    ListStoriesScreen(navController = navController)
}
