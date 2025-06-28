package com.example.proyectodegrado.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

// Estado para manner la subida de imagen
//sealed class UploadImageState {
//    object Idle : UploadImageState()
//    object Loading : UploadImageState()
//    data class Error(val message: String) : UploadImageState()
//}

@Composable
fun CircularImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    placeholder: @Composable () -> Unit
) {
    if (!imageUrl.isNullOrBlank()) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Imagen",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(CircleShape)
                .background(Color.Gray)
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            placeholder()
        }
    }
}

@Composable
fun ImagePickerButton(
    modifier: Modifier = Modifier,
    currentImageUrl: String?,
    uploadState: UploadImageState,
    onImageSelected: (Uri?) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> onImageSelected(uri) }
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .clickable {
                if (uploadState !is UploadImageState.Loading) {
                    imagePickerLauncher.launch("image/*")
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (uploadState is UploadImageState.Loading) {
            CircularProgressIndicator()
        } else {
            CircularImage(
                modifier = Modifier.matchParentSize(),
                imageUrl = currentImageUrl,
                placeholder = {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Seleccionar Imagen",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}