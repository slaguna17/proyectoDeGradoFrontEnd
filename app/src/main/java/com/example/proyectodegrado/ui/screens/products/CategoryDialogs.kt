package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CreateCategoryFormState
import com.example.proyectodegrado.ui.components.SelectImage
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.launch

@Composable
fun CreateCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    // --- PARÁMETROS NUEVOS ---
    // Recibe estados y callbacks del ViewModel
    formState: CreateCategoryFormState,          // Estado completo del formulario
    imageUploadState: UploadImageState,      // Estado actual de la subida de imagen
    onFormStateChange: (CreateCategoryFormState) -> Unit, // Callback para actualizar estado en ViewModel
    onImageUriSelected: (Uri?) -> Unit,      // Callback para notificar al ViewModel la selección de Uri
    onCreateClick: () -> Unit                // Callback para decirle al ViewModel que cree la categoría
    // Ya no se reciben: onCreate(String,String,String), name, onNameChange, description, onDescriptionChange, image, onImageChange
) {
    if (show) {
        // Ya no necesitamos el estado local 'selectedBitmap' ni 'coroutineScope' aquí

        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) { // Añadido scroll por si acaso
                    Text("Crear Categoría", style = MaterialTheme.typography.h6) // Estilo M3
                    Spacer(Modifier.height(16.dp))

                    // --- Campos de Texto ---
                    // Leen de 'formState' y actualizan usando 'onFormStateChange'
                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { newValue -> onFormStateChange(formState.copy(name = newValue)) },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { newValue -> onFormStateChange(formState.copy(description = newValue)) },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    // --- Componente de Imagen ---
                    // Ya no se usa 'SelectImage', se usa el nuevo 'UploadImage'
                    UploadImage(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        currentImageUrl = formState.imageUrl, // Muestra la URL del estado
                        uploadState = imageUploadState, // Pasa el estado de subida
                        onImageSelected = { selectedUri ->
                            // Llama al callback que notificará al ViewModel para iniciar la subida
                            onImageUriSelected(selectedUri)
                        }
                    )
                    // Muestra error de subida si existe
                    if (imageUploadState is UploadImageState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Error imagen: ${imageUploadState.message}",
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    // --------------------------

                    Spacer(Modifier.height(16.dp))

                    // --- Botones de Acción ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = onCreateClick, // Llama al callback para crear (ViewModel tiene los datos)
                            // Deshabilita el botón si la imagen se está cargando/subiendo
                            enabled = imageUploadState == UploadImageState.Idle || imageUploadState is UploadImageState.Error
                        ) {
                            Text("Crear")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onEdit: (Int, String, String, String) -> Unit,
    category: Category?
) {
    if (show && category != null) {
        var editedName by remember { mutableStateOf(category.name) }
        var editedDescription by remember { mutableStateOf(category.description) }
        var editedImage by remember { mutableStateOf(category.image) }


        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Categoría", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = editedDescription, onValueChange = { editedDescription = it }, label = { Text("Descripción") })
//                    uploadImage(
//                        buttonText = "Elegir foto de categoria",
//                        onUploadResult = { result ->
//                            result.fold(
//                                onSuccess = { url -> onImageChange(url) },
//                                onFailure = { error ->
//                                    // Aquí puedes mostrar un mensaje de error o registrar la falla.
//                                    onImageChange("")  // O mantener el campo vacío
//                                }
//                            )
//                        }
//                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onEdit(category.id, editedName, editedDescription,editedImage); onDismiss() }) { Text("Guardar") }
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
    onDelete: () -> Unit,
    category: Category?
) {
    if (show && category != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar la categoria: ${category.name}?", style = MaterialTheme.typography.h6)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onDelete(); onDismiss() }) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}