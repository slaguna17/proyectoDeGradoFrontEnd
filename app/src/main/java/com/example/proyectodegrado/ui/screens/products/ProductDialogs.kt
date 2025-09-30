package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState
import com.example.proyectodegrado.ui.screens.products.StoreOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialogContent(
    title: String,
    formState: CreateProductFormState,
    uploadState: UploadImageState,
    availableCategories: List<Category>,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onStockChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    onAdjustStockClick: () -> Unit,
    submitLabel: String
) {
    var expanded by remember { mutableStateOf(false) }
    val isFormValid = formState.name.isNotBlank() && formState.categoryId != -1

    Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(vertical = 16.dp)) {
        Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = formState.name, onValueChange = onNameChange, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = formState.sku, onValueChange = onSkuChange, label = { Text("SKU (Opcional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = formState.description, onValueChange = onDescriptionChange, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = formState.brand, onValueChange = onBrandChange, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                val selectedCategoryName = availableCategories.find { it.id == formState.categoryId }?.name ?: ""
                OutlinedTextField(value = selectedCategoryName, onValueChange = {}, readOnly = true, label = { Text("Categoría") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    availableCategories.forEach { category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = { onCategorySelected(category.id); expanded = false })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = formState.stock, onValueChange = onStockChange, label = { Text(if (isEditMode) "Stock (No editable)" else "Stock Inicial") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), enabled = !isEditMode)
            Spacer(Modifier.height(16.dp))

            if (isEditMode) {
                OutlinedButton(
                    onClick = onAdjustStockClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Ajustar Stock")
                    Spacer(Modifier.width(8.dp))
                    Text("Ajustar Stock Manualmente")
                }
                Spacer(Modifier.height(16.dp))
            }

            val imageUrl = formState.localImageUri?.toString() ?: formState.imageUrl
            UploadImage(modifier = Modifier.align(Alignment.CenterHorizontally), currentImageUrl = imageUrl, uploadState = uploadState, onImageSelected = onImageSelected)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(enabled = isFormValid && uploadState is UploadImageState.Idle, onClick = onSubmit) { Text(submitLabel) }
            }
        }
    }
}

@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateProductFormState,
    imageUploadState: UploadImageState,
    availableCategories: List<Category>,
    onNameChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onStockChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onCreateClick: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        ProductDialogContent(
            title = "Crear Producto",
            formState = formState,
            uploadState = imageUploadState,
            availableCategories = availableCategories,
            isEditMode = false,
            onNameChange = onNameChange,
            onSkuChange = onSkuChange,
            onDescriptionChange = onDescriptionChange,
            onBrandChange = onBrandChange,
            onCategorySelected = onCategorySelected,
            onStockChange = onStockChange,
            onImageSelected = onImageSelected,
            onDismiss = onDismiss,
            onAdjustStockClick = {},
            onSubmit = onCreateClick,
            submitLabel = "Crear"
        )
    }
}

@Composable
fun EditProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateProductFormState,
    imageUploadState: UploadImageState,
    availableCategories: List<Category>,
    onNameChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onStockChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onAdjustStockClick: () -> Unit,
    onEditClick: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        ProductDialogContent(
            title = "Editar Producto",
            formState = formState,
            uploadState = imageUploadState,
            availableCategories = availableCategories,
            isEditMode = true,
            onAdjustStockClick = onAdjustStockClick,
            onNameChange = onNameChange,
            onSkuChange = onSkuChange,
            onDescriptionChange = onDescriptionChange,
            onBrandChange = onBrandChange,
            onCategorySelected = onCategorySelected,
            onStockChange = onStockChange,
            onImageSelected = onImageSelected,
            onDismiss = onDismiss,
            onSubmit = onEditClick,
            submitLabel = "Guardar"
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    productName: String,
    availableStores: List<StoreOption>, // Usaremos la misma clase de datos del filtro
    onAssign: (storeId: Int, stock: String) -> Unit
) {
    if (!show) return

    var selectedStoreId by remember { mutableStateOf<Int?>(null) }
    var stock by remember { mutableStateOf("0") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isFormValid = selectedStoreId != null && stock.isNotBlank() && stock.toIntOrNull() != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Asignar \"$productName\"",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Selecciona una tienda y define el stock inicial para añadir este producto.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))

                // Dropdown para seleccionar la tienda
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    val selectedStoreName = availableStores.find { it.id == selectedStoreId }?.name ?: ""
                    OutlinedTextField(
                        value = selectedStoreName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tienda") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        availableStores.forEach { store ->
                            DropdownMenuItem(
                                text = { Text(store.name) },
                                onClick = {
                                    selectedStoreId = store.id
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                // Campo para el stock
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onAssign(selectedStoreId!!, stock) },
                        enabled = isFormValid
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveAssignmentDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    productName: String,
    storeName: String,
    onConfirm: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Acción") },
        text = { Text("¿Estás seguro de que deseas quitar el producto \"$productName\" de la tienda \"$storeName\"?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Quitar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AdjustStockDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    productName: String,
    currentStock: String,
    onConfirm: (newStock: String) -> Unit
) {
    if (!show) return

    var newStock by remember { mutableStateOf(currentStock) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar Stock de \"$productName\"") },
        text = {
            Column {
                Text("Ingresa la nueva cantidad de unidades para este producto.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = newStock,
                    onValueChange = { newStock = it },
                    label = { Text("Nueva Cantidad de Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newStock) },
                enabled = newStock.isNotBlank() && newStock.toIntOrNull() != null
            ) {
                Text("Confirmar Ajuste")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}