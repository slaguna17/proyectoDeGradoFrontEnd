package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.ui.components.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
                        text = "Error imagen: ${(imageUploadState as UploadImageState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onCreateClick) { Text("Crear") }
                }
            }
        }
    }
}

@Composable
fun EditCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    category: Category,
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

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val request = CategoryRequest(name, description, category.image)
                        onEdit(request)
                    }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

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
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "¿Estás seguro de que deseas eliminar la categoría \"${category.name}\"?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
