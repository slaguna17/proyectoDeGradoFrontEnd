package com.example.proyectodegrado.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

// --- Ui Status for the component ---
sealed class UploadImageState {
    object Idle : UploadImageState() // Initial status or status after success/error solved
    object Loading : UploadImageState() // Upload presigned or confirmed URL
    object Uploading : UploadImageState() // Byte upload to S3
    data class Error(val message: String) : UploadImageState()
}

@Composable
fun UploadImage(
    modifier: Modifier = Modifier,
    currentImageUrl: String?,
    uploadState: UploadImageState = UploadImageState.Idle,
    imageSize: Dp = 100.dp,
    onImageSelected: (Uri?) -> Unit
) {
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelected(uri) }
    )
    val placeholderPainter = rememberVectorPainter(Icons.Default.Image)

    val launchPicker = {
        if (uploadState == UploadImageState.Idle) {
            pickMediaLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    Box(
        modifier = modifier.size(imageSize + 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- Image Container ---
        Box(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(onClick = launchPicker),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentImageUrl,
                contentDescription = "Imagen del producto",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = placeholderPainter,
                fallback = placeholderPainter
            )

            when (uploadState) {
                is UploadImageState.Uploading, is UploadImageState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is UploadImageState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error de subida",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(imageSize / 3)
                        )
                    }
                }
                is UploadImageState.Idle -> { /* No Overlay */ }
            }
        }

        SmallFloatingActionButton(
            onClick = launchPicker,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp),
            containerColor = MaterialTheme.colorScheme.surface,
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Cambiar foto"
            )
        }
    }
}