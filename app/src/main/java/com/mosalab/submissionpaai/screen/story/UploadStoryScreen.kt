package com.mosalab.submissionpaai.screen.story

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mosalab.submissionpaai.PreferencesManager
import com.mosalab.submissionpaai.R
import com.mosalab.submissionpaai.api.ApiService
import com.mosalab.submissionpaai.data.AddStoryResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import android.content.pm.PackageManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun UploadStoryScreen(navController: NavController) {
    val context = LocalContext.current
    val token = remember { mutableStateOf<String?>(null) }
    var storyText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var uploadResult by remember { mutableStateOf<AddStoryResponse?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        token.value = PreferencesManager(context).token.firstOrNull()
    }

    val hasCameraPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasCameraPermission) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), 1)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && imageUri != null) {
            imageBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
        } else {
            imageUri = null
            imageBitmap = null
            Toast.makeText(context, "Image capture was cancelled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imageBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        } else {
            imageBitmap = null
            Toast.makeText(context, "Image selection was cancelled.", Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadStory() {
        if (storyText.isNotBlank()) {
            if (imageUri != null) {
                isLoading = true

                if (imageUri == null) {
                    Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                    return
                }

                val photoFile = uriToFile(imageUri!!, context)

                val photoRequestBody = photoFile.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, photoRequestBody)

                val descriptionRequestBody =
                    storyText.toRequestBody("text/plain".toMediaTypeOrNull())

                coroutineScope.launch {
                    try {
                        val response = ApiService.api.uploadStory(
                            token = "Bearer ${token.value!!}",
                            description = descriptionRequestBody,
                            photo = photoPart
                        )

                        isLoading = false
                        if (response.isSuccessful) {
                            uploadResult = response.body()
                            Log.d("UploadStory", "Success: ${uploadResult?.message}")
                            navController.popBackStack()
                        } else {
                            Log.e("UploadStory", "Error: ${response.message()}")
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        Log.e("UploadStory", "Failure: ${e.message}")
                    }
                }
            } else {
                Toast.makeText(context, "Please add a photo to your story.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please write something before uploading.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = "Uploaded Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.baseline_insert_photo_24),
                contentDescription = "Upload Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
        }

        Row {
            Button(onClick = {
                if (hasCameraPermission) {
                    val photoUri = createImageUri(context)
                    imageUri = photoUri
                    cameraLauncher.launch(photoUri)
                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), 1)
                }
            }) {
                Text("Open Camera")
            }

            Spacer(Modifier.width(20.dp))

            Button(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Open Gallery")
            }
        }

        Spacer(Modifier.width(40.dp))

        OutlinedTextField(
            value = storyText,
            onValueChange = { storyText = it },
            label = { Text("Write your story") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { uploadStory() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isLoading
        ) {
            Text("Upload Story")
        }

        // Loading Indicator
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.TITLE, "New Story Image")
        put(android.provider.MediaStore.Images.Media.DESCRIPTION, "Story Image")
    }
    return context.contentResolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ) ?: throw Exception("Unable to create image URI")
}

private fun uriToFile(uri: Uri, context: Context): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "uploaded_image.jpg")
    try {
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

@Preview(showBackground = true)
@Composable
fun UploadStoryScreenPreview() {
    val context = LocalContext.current
    UploadStoryScreen(navController = NavController(context = context))
}
