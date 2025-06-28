package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.ui.components.UploadImageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateProductFormState,
    imageUploadState: UploadImageState,
    availableCategories: List<Category>,
    onFormStateChange: (CreateProductFormState) -> Unit,
    onImageUriSelected: (Uri?) -> Unit,
    onCreateClick: () -> Unit
) {
    if (!show) return

    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(vertical = 16.dp)) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Crear Producto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                // Fila para Imagen y campos principales
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        ImagePickerButton(
//                            currentImageUrl = formState.imageUrl,
//                            uploadState = imageUploadState,
//                            onImageSelected = onImageUriSelected
//                        )
                    }
                    Column(Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = formState.name,
                            onValueChange = { onFormStateChange(formState.copy(name = it)) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = formState.sku,
                            onValueChange = { onFormStateChange(formState.copy(sku = it)) },
                            label = { Text("SKU") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = { onFormStateChange(formState.copy(description = it)) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.brand,
                    onValueChange = { onFormStateChange(formState.copy(brand = it)) },
                    label = { Text("Marca") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                // Selector de Categoría
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    val selectedCategoryName = availableCategories.find { it.id == formState.categoryId }?.name ?: ""
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onFormStateChange(formState.copy(categoryId = category.id))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.stock,
                    onValueChange = { onFormStateChange(formState.copy(stock = it)) },
                    label = { Text("Stock Inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = onCreateClick) { Text("Crear") }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    product: Product?,
    availableCategories: List<Category>,
    onEditClick: (CreateProductFormState) -> Unit
) {
    if (!show || product == null) return

    var formState by remember { mutableStateOf(
        CreateProductFormState(
            name = product.name,
            description = product.description,
            sku = product.sku ?: "",
            brand = product.brand,
            categoryId = product.categoryId,
            imageUrl = product.image,
            // El stock se edita en otra parte según el modelo, pero lo incluimos por si acaso
            stock = "0"
        )
    ) }

    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(vertical = 16.dp)) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Editar Producto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = formState.name, onValueChange = { formState = formState.copy(name = it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = formState.sku, onValueChange = { formState = formState.copy(sku = it) }, label = { Text("SKU") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = formState.description, onValueChange = { formState = formState.copy(description = it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = formState.brand, onValueChange = { formState = formState.copy(brand = it) }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    val selectedCategoryName = availableCategories.find { it.id == formState.categoryId }?.name ?: ""
                    OutlinedTextField(value = selectedCategoryName, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = { formState = formState.copy(categoryId = category.id); expanded = false })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = { onEditClick(formState) }) { Text("Guardar") }
                }
            }
        }
    }
}


@Composable
fun DeleteProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    product: Product?,
    onDelete: () -> Unit
) {
    if (!show || product == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar el producto \"${product.name}\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}