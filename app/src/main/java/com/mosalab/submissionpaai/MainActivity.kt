package com.mosalab.submissionpaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mosalab.submissionpaai.screen.login.LandingScreen
import com.mosalab.submissionpaai.screen.login.LoginScreen
import com.mosalab.submissionpaai.screen.login.RegisterScreen
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
                        navController.navigate("home")
                    } else {
                        navController.navigate("landing")
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = "landing") {
                        composable("landing") {
                            LandingScreen(navController, modifier = Modifier.padding(innerPadding))
                        }
                        composable("login") {
                            LoginScreen(navController, modifier = Modifier.padding(innerPadding))
                        }
                        composable("register") {
                            RegisterScreen(navController, modifier = Modifier.padding(innerPadding))
                        }
                        composable("home") {
                            ListStoriesScreen(navController, modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}
