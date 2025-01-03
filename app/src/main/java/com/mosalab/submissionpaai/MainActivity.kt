package com.mosalab.submissionpaai

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mosalab.submissionpaai.Maps.MapsScreen
import com.mosalab.submissionpaai.screen.landing.LandingScreen
import com.mosalab.submissionpaai.screen.login.LoginScreen
import com.mosalab.submissionpaai.screen.register.RegisterScreen
import com.mosalab.submissionpaai.screen.story.DetailListStoryScreen
import com.mosalab.submissionpaai.screen.story.ListStoriesScreen
import com.mosalab.submissionpaai.screen.story.UploadStoryScreen
import com.mosalab.submissionpaai.ui.theme.SubmissionPAAITheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with camera functionality
            setupUI()
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.UserPreferencesDataStore.initialize(applicationContext)

        // Check if the app has camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // Permission already granted, you can proceed with the camera functionality
            setupUI()
        }
    }

    private fun setupUI() {
        setContent {
            SubmissionPAAITheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    val token = PreferencesManager(applicationContext).token.first()
                    if (token != null) {
                        navigateToHome(navController)
                    } else {
                        navigateToLanding(navController)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "landing",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("landing") {
                            LandingScreen(navController)
                            HandleBackPress(navController, exitApp = true)
                        }
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("register") {
                            RegisterScreen(navController)
                        }
                        composable("home") {
                            ListStoriesScreen(navController)
                            HandleBackPress(navController, exitApp = true)
                        }
                        composable("detail/{storyId}") { backStackEntry ->
                            val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                            DetailListStoryScreen(navController, storyId)
                        }
                        composable("uploadStory") {
                            UploadStoryScreen(navController)
                        }

                        composable("maps") {
                            MapsScreen(navController)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HandleBackPress(navController: NavController, exitApp: Boolean = false) {
        BackHandler {
            if (exitApp) {
                (navController.context as? ComponentActivity)?.finish()
            } else {
                navController.popBackStack()
            }
        }
    }

    private fun navigateToHome(navController: NavController) {
        navController.navigate("home") {
            popUpTo("landing") { inclusive = true }
        }
    }

    private fun navigateToLanding(navController: NavController) {
        navController.navigate("landing") {
            popUpTo("home") { inclusive = true }
        }
    }
}
