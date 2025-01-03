package com.mosalab.submissionpaai.screen.login

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lint.kotlin.metadata.Visibility
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.R
import com.mosalab.submissionpaai.data.LoginRequest
import com.mosalab.submissionpaai.data.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val visibilityState = remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var showPasswordErrorToast by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visibilityState.value = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = fadeIn(tween(1500)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500)
            )
        ) {
            Image(
                painter = painterResource(R.drawable.image_login),
                contentDescription = "Login Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = scaleIn(tween(1000)) + fadeIn(tween(1000))
        ) {
            Text(
                text = stringResource(R.string.title_login_page),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(tween(800))
        ) {
            Text(
                text = stringResource(R.string.message_login_page),
                fontSize = 16.sp,
                fontWeight = FontWeight.W400
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(1000)
            )
        ) {
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                if (it.length < 8) {
                    isPasswordValid = false
                    showPasswordErrorToast = true
                } else {
                    isPasswordValid = true
                    showPasswordErrorToast = false
                }
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password Icon"
                )
            },
            trailingIcon = {
                val image = if (isPasswordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier.fillMaxWidth(),
            isError = !isPasswordValid
        )

        LaunchedEffect(showPasswordErrorToast) {
            if (showPasswordErrorToast) {
                Toast.makeText(context, "Password must be more than 8 characters", Toast.LENGTH_SHORT).show()
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800)
            ) + fadeIn(tween(1000))
        ) {
            Button(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                        isLoading.value = true
                        coroutineScope.launch {
                            try {
                                val loginRequest = LoginRequest(
                                    email = email.value,
                                    password = password.value
                                )

                                ApiService.api.login(loginRequest)
                                    .enqueue(object : Callback<LoginResponse> {
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            isLoading.value = false
                                            if (response.isSuccessful) {
                                                val loginResponse = response.body()
                                                if (loginResponse != null && !loginResponse.error) {
                                                    val token = loginResponse.loginResult?.token
                                                    if (!token.isNullOrEmpty()) {
                                                        coroutineScope.launch {
                                                            PreferencesManager(context).saveToken(token = token)
                                                        }
                                                        navController.navigate("home")
                                                    } else {
                                                        errorMessage.value = "Token is empty."
                                                    }
                                                } else {
                                                    errorMessage.value =
                                                        loginResponse?.message ?: "Unknown error"
                                                }
                                            } else {
                                                errorMessage.value =
                                                    "Login failed with status: ${response.code()} ${response.message()}"
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<LoginResponse>,
                                            t: Throwable
                                        ) {
                                            isLoading.value = false
                                            errorMessage.value = "Error logging in: ${t.message}"
                                            Toast.makeText(
                                                context,
                                                "Error: ${t.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            } catch (e: Exception) {
                                isLoading.value = false
                                errorMessage.value = "Error logging in: ${e.message}"
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        errorMessage.value = "Please fill in all fields"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Login")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage.value?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = visibilityState.value,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(1200)
            ) + fadeIn(tween(1200))
        ) {
            TextButton(onClick = { navController.navigate("register") }) {
                Text(text = "Don't have an account? Register here")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController)
}
