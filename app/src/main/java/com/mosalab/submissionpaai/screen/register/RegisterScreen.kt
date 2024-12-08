package com.mosalab.submissionpaai.screen.register

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mosalab.submissionpaai.R
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.RegisterRequest
import com.mosalab.submissionpaai.data.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavController, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var showPasswordErrorToast by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) } // To toggle password visibility
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(1500)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(500)
            )
        ) {
            Image(
                painter = painterResource(R.drawable.image_signup),
                contentDescription = "Login Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(tween(1000)) + fadeIn(tween(1000))
        ) {
            Text(
                text = stringResource(R.string.title_signup_page),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(tween(800))
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(10.dp))

        AnimatedVisibility(
            visible = true,
            enter = expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(1000)
            )
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(10.dp))

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(tween(1000)) + fadeIn(tween(1000))
        ) {


            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.length <= 8) {
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
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
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
                    showPasswordErrorToast = false
                }
            }
        }



        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800)
            ) + fadeIn(tween(1000))
        ) {
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isLoading.value) return@Button

                    isLoading.value = true
                    val registerRequest = RegisterRequest(name, email, password)

                    ApiService.api.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            isLoading.value = false
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "User Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("login")
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error: ${response.message()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            isLoading.value = false
                            Toast.makeText(
                                context,
                                "Request Failed: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "Register")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800)
            ) + fadeIn(tween(1000))
        ) {
            TextButton(onClick = { navController.navigate("login") }) {
                Text(text = "Already have an account? Login here")
            }
        }
    }
}
