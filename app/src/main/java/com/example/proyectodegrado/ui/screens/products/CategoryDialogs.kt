package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.ui.components.uploadImage

@Composable
fun CreateCategoryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    image: String,
    onImageChange:(String) -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Categoría", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre") })
                    OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text("Descripción") })
                    uploadImage(buttonText = "Elegir foto de categoria")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, description,image); onDismiss() }) { Text("Crear") }
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
                    uploadImage(buttonText = "Cambiar foto de categoria")
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