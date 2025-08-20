package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.ui.components.*

// ---------- Crear ----------
@Composable
fun CreateCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateCategoryFormState,
    imageUploadState: UploadImageState,
    onFormStateChange: (CreateCategoryFormState) -> Unit,
    onImageUriSelected: (Uri?) -> Unit,
    onCreateClick: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Crear Categoría", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { onFormStateChange(formState.copy(name = it)) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = formState.description,
                    onValueChange = { onFormStateChange(formState.copy(description = it)) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                UploadImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    currentImageUrl = formState.imageUrl,
                    uploadState = imageUploadState,
                    onImageSelected = onImageUriSelected
                )
                if (imageUploadState is UploadImageState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Error imagen: ${imageUploadState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        enabled = imageUploadState is UploadImageState.Idle,
                        onClick = onCreateClick
                    ) { Text("Crear") }
                }
            }
        }
    }
}

// ---------- Editar ----------
@Composable
fun EditCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    category: Category,
    imageUploadState: UploadImageState,
    pendingImageKey: String?,                    // viene del VM.editImageKey
    onPickNewImage: (Uri?) -> Unit,              // VM.selectImageForEdit(category.id, uri)
    onClearPendingImage: () -> Unit,
    onEdit: (CategoryRequest) -> Unit
) {
    if (!show) return

    var name by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Editar Categoría", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                UploadImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    currentImageUrl = category.imageUrl,   // la actual
                    uploadState = imageUploadState,
                    onImageSelected = onPickNewImage
                )
                if (imageUploadState is UploadImageState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Error imagen: ${imageUploadState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onClearPendingImage()
                        onDismiss()
                    }) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        enabled = imageUploadState is UploadImageState.Idle,
                        onClick = {
                            val request = CategoryRequest(
                                name = name.trim(),
                                description = description.trim(),
                                imageKey = pendingImageKey ?: category.image // usa la nueva key si existe
                            )
                            onEdit(request)
                            onClearPendingImage()
                        }
                    ) { Text("Guardar") }
                }
            }
        }
    }
}

// ---------- Eliminar ----------
@Composable
fun DeleteCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    category: Category,
    onDelete: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(16.dp)) {
                Text("Eliminar Categoría", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("¿Seguro que deseas eliminar \"${category.name}\"?")
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) { Text("Eliminar") }
                }
            }
        }
    }
}
