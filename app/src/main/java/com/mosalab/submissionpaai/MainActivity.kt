package com.mosalab.submissionpaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mosalab.submissionpaai.screen.login.LandingScreen
import com.mosalab.submissionpaai.screen.login.LoginScreen
import com.mosalab.submissionpaai.screen.login.RegisterScreen
import com.mosalab.submissionpaai.screen.story.DetailListStoryScreen
import com.mosalab.submissionpaai.screen.story.ListStoriesScreen
import com.mosalab.submissionpaai.ui.theme.SubmissionPAAITheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.UserPreferencesDataStore.initialize(applicationContext)
        enableEdgeToEdge()

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

fun navigateToHome(navController: NavController) {
    navController.navigate("home") {
        popUpTo("landing") { inclusive = true }
    }
}

fun navigateToLanding(navController: NavController) {
    navController.navigate("landing") {
        popUpTo("home") { inclusive = true }
    }
}
