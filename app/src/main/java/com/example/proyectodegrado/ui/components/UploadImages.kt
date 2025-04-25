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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

// --- Estado de la UI para el componente ---
sealed class UploadImageState {
    object Idle : UploadImageState() // Estado inicial o después de éxito/error resuelto
    object Loading : UploadImageState() // Cargando URL prefirmada o confirmando
    object Uploading : UploadImageState() // Subiendo bytes a S3
    data class Error(val message: String) : UploadImageState()
    // Success no necesita estado aquí, se refleja en currentImageUrl cambiando
}
// -----------------------------------------

/**
 * Un Composable reutilizable para seleccionar y mostrar una imagen,
 * indicando visualmente el estado de una operación de subida.
 *
 * @param modifier Modificador estándar de Compose.
 * @param currentImageUrl URL de la imagen a mostrar actualmente (puede ser null).
 * @param uploadState Estado actual del proceso de subida (controlado externamente).
 * @param placeholder Icono a mostrar cuando no hay imagen.
 * @param errorPlaceholder Icono a mostrar si falla la carga de `currentImageUrl`.
 * @param imageSize Tamaño del área de la imagen.
 * @param isUploading Determina si mostrar el indicador de progreso.
 * @param errorMessage Mensaje de error a mostrar (si uploadState es Error).
 * @param onImageSelected Lambda que se llama con el Uri de la imagen seleccionada por el usuario.
 * El llamador (ViewModel) debe iniciar la subida real al recibir este Uri.
 */
@Composable
fun UploadImage(
    modifier: Modifier = Modifier,
    currentImageUrl: String?,
    uploadState: UploadImageState = UploadImageState.Idle,
    placeholder: ImageVector = Icons.Default.AddAPhoto,
    errorPlaceholder: ImageVector = Icons.Default.BrokenImage,
    imageSize: Dp = 120.dp,
    onImageSelected: (Uri?) -> Unit // Callback con el Uri seleccionado
) {
    val context = LocalContext.current

    // Launcher para el selector de imágenes moderno (PickVisualMedia)
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            // Llama al callback con el Uri seleccionado (puede ser null si el usuario cancela)
            onImageSelected(uri)
        }
    )

    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape) // Forma circular (puedes cambiarla)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clickable(enabled = uploadState == UploadImageState.Idle) { // Solo clickeable si está inactivo
                // Lanzar el selector de imágenes visuales
                pickMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // --- Contenido de la Imagen ---
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(currentImageUrl) // Carga la URL actual
                .crossfade(true) // Efecto de transición suave
                .build(),
            contentDescription = "Imagen Seleccionada",
            contentScale = ContentScale.Crop, // Ajusta cómo se escala la imagen
            modifier = Modifier.fillMaxSize()
        ) {
            val state = painter.state
            when (state) {
                is AsyncImagePainter.State.Loading -> {
                    // Placeholder mientras carga la imagen desde la URL
                    CircularProgressIndicator(modifier = Modifier.size(imageSize / 2))
                }
                is AsyncImagePainter.State.Error -> {
                    // Placeholder si falla la carga desde la URL
                    Icon(
                        imageVector = errorPlaceholder,
                        contentDescription = "Error de carga",
                        modifier = Modifier.size(imageSize / 2),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                is AsyncImagePainter.State.Success -> {
                    // Muestra la imagen si se cargó correctamente
                    SubcomposeAsyncImageContent()
                }
                is AsyncImagePainter.State.Empty -> {
                    // Placeholder si no hay URL o está vacía
                    Icon(
                        imageVector = placeholder,
                        contentDescription = "Seleccionar Imagen",
                        modifier = Modifier.size(imageSize / 2),
                        tint = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            }
        } // Fin de SubcomposeAsyncImage

        // --- Overlay de Estado de Subida ---
        when (uploadState) {
            is UploadImageState.Loading, is UploadImageState.Uploading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)), // Fondo semitransparente
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
                    // Podrías mostrar uploadState.message aquí también con un Text
                }
            }
            is UploadImageState.Idle -> {
                // No mostrar overlay en estado Idle
            }
        } // Fin de when(uploadState)
    } // Fin del Box principal
}
