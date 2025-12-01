package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.ui.components.*

@Composable
private fun CategoryDialogContent(
    title: String,
    formState: CreateCategoryFormState,
    uploadState: UploadImageState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    submitLabel: String
) {
    Surface(shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.name.isBlank()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            val imageUrl = formState.localImageUri?.toString() ?: formState.imageUrl
            UploadImage(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                currentImageUrl = imageUrl,
                uploadState = uploadState,
                onImageSelected = onImageSelected
            )
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(
                    enabled = uploadState is UploadImageState.Idle && formState.name.isNotBlank(),
                    onClick = onSubmit
                ) { Text(submitLabel) }
            }
        }
    }
}

// ---------- Create ----------
@Composable
fun CreateCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateCategoryFormState,
    imageUploadState: UploadImageState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onCreateClick: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        CategoryDialogContent(
            title = "Crear Categoría",
            formState = formState,
            uploadState = imageUploadState,
            onNameChange = onNameChange,
            onDescriptionChange = onDescriptionChange,
            onImageSelected = onImageSelected,
            onDismiss = onDismiss,
            onSubmit = onCreateClick,
            submitLabel = "Crear"
        )
    }
}

// ---------- Edit ----------
@Composable
fun EditCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateCategoryFormState,
    imageUploadState: UploadImageState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onEdit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        CategoryDialogContent(
            title = "Editar Categoría",
            formState = formState,
            uploadState = imageUploadState,
            onNameChange = onNameChange,
            onDescriptionChange = onDescriptionChange,
            onImageSelected = onImageSelected,
            onDismiss = onDismiss,
            onSubmit = onEdit,
            submitLabel = "Guardar"
        )
    }
}

// ---------- Delete ----------
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
