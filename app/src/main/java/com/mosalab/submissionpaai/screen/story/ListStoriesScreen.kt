package com.mosalab.submissionpaai.screen.story

import StoryViewModelFactory
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.DataStory
import com.mosalab.submissionpaai.viewmodel.StoryViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStoriesScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDropdownExpanded = remember { mutableStateOf(false) }

    // Ambil token dari PreferencesManager
    val token = remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        token.value = PreferencesManager(context).token.firstOrNull()
    }

    // Deklarasi ViewModel
    val viewModel: StoryViewModel = token.value?.let {
        androidx.lifecycle.viewmodel.compose.viewModel(factory = StoryViewModelFactory(it))
    } ?: return

    // Paging data
    val stories = viewModel.storyPagingData.collectAsLazyPagingItems()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dicoding Stories", color = Color.Black) },
                actions = {
                    Box {

                        Row {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Maps",
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        navController.navigate("maps")
                                    }

                            )
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Settings",
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable {
                                        isDropdownExpanded.value = true
                                    }
                            )
                            DropdownMenu(
                                expanded = isDropdownExpanded.value,
                                onDismissRequest = { isDropdownExpanded.value = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    onClick = {
                                        isDropdownExpanded.value = false
                                        showToast(context, "Settings clicked")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = {
                                        isDropdownExpanded.value = false
                                        coroutineScope.launch {
                                            PreferencesManager(context).clearSession()
                                            navController.navigate("landing")
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("uploadStory") }) {
                Icon(Icons.Filled.Add, contentDescription = "Upload Story")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            if (stories.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(stories.itemCount) { index ->
                        val story = stories[index]
                        if (story != null) {
                            StoryListItem(story = story) {
                                navController.navigate("detail/${story.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun StoryListItem(story: DataStory, onClick: () -> Unit) {
    var clicked by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !clicked,
        enter = scaleIn(tween(500)) + fadeIn(tween(500)),
        exit = scaleOut(tween(500)) + fadeOut(tween(500))
    ) {
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
                    maxLines = 3,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListStoriesScreenPreview() {
    val navController = rememberNavController()

    ListStoriesScreen(navController = navController)
}
