package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.ProductRequest2
import com.example.proyectodegrado.ui.components.UploadImageState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.ui.components.UploadImage


@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateProductFormState,               // <-- Recibe el objeto completo
    imageUploadState: UploadImageState,
    onFormStateChange: (CreateProductFormState) -> Unit, // <-- Callback para actualizar TODO el estado
    onImageUriSelected: (Uri?) -> Unit,
    onCreateClick: (categoryId: Int, storeId: Int) -> Unit
) {
    val exampleCategoryId = formState.categoryId
    val exampleStoreId = 1 // Reemplaza con el ID real

    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text("Crear Producto", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(16.dp))

                    // --- Uso correcto de formState y onFormStateChange ---
                    OutlinedTextField(
                        value = formState.name, // Lee del estado
                        onValueChange = { newValue ->
                            onFormStateChange(formState.copy(name = newValue)) // Actualiza copiando el estado
                        },
                        label = { Text("Nombre del producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = { newValue ->
                            onFormStateChange(formState.copy(description = newValue)) // Copia y actualiza
                        },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // ... Repite el patrón para sku, brand, stock, expirationDate ...
                    OutlinedTextField(
                        value = formState.sku,
                        onValueChange = { onFormStateChange(formState.copy(sku = it)) },
                        label = { Text("SKU (Código)") },
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
                    OutlinedTextField(
                        value = formState.stock, // formState.stock es String
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                onFormStateChange(formState.copy(stock = newValue))
                            }
                        },
                        label = { Text("Stock inicial") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.expirationDate,
                        onValueChange = { onFormStateChange(formState.copy(expirationDate = it)) },
                        label = { Text("Fecha expiración (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // ----------------------------------------------------

                    Spacer(Modifier.height(16.dp))

                    UploadImage(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        currentImageUrl = formState.imageUrl, // Lee la URL del estado
                        uploadState = imageUploadState,
                        onImageSelected = { selectedUri ->
                            onImageUriSelected(selectedUri) // Llama al callback del ViewModel
                        }
                    )
                    if (imageUploadState is UploadImageState.Error) { /* Muestra error */ }

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { onCreateClick(exampleCategoryId, exampleStoreId) },
                            enabled = imageUploadState == UploadImageState.Idle || imageUploadState is UploadImageState.Error
                        ) { Text("Crear") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    //(id, name, description, image, SKU, brand, category_id, stock, expiration_date
    onEdit: (Int, String, String, String, String, String, Int, Int, String) -> Unit,
    productRequest: ProductRequest2?
) {
    if (show && productRequest != null) {
        var editedName by remember { mutableStateOf(productRequest.product.name) }
        var editedDescription by remember { mutableStateOf(productRequest.product.description) }
        var editedImage by remember { mutableStateOf(productRequest.product.image) }
        var editedSku by remember { mutableStateOf(productRequest.product.SKU) }
        var editedBrand by remember { mutableStateOf(productRequest.product.brand) }
        var editedCategory by remember { mutableIntStateOf(productRequest.product.category_id) }
        var editedStock by remember { mutableIntStateOf(productRequest.store.stock) }
        var editedDate by remember { mutableStateOf(productRequest.store.expiration_date) }

        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Producto", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre del producto") })
                    OutlinedTextField(value = editedDescription, onValueChange = { editedDescription = it }, label = { Text("Descripción del producto") })
                    OutlinedTextField(value = editedSku, onValueChange = { editedSku = it }, label = { Text("SKU del producto") })
                    OutlinedTextField(value = editedBrand, onValueChange = { editedBrand = it }, label = { Text("Marca del producto") })
                    OutlinedTextField(value = editedStock.toString(), onValueChange = { editedStock = it.toInt() }, label = { Text("Stock") })
                    OutlinedTextField(value = editedDate, onValueChange = {editedDate = it}, label = { Text("Fecha de expiracion") })
//                    uploadImage(
//                        buttonText = "Elegir foto del producto",
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
                        Button(onClick = { onEdit(productRequest.product.id, editedName, editedDescription,editedImage, editedSku, editedBrand, editedCategory, editedStock, editedDate); onDismiss() }) { Text("Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    product: Product?
) {
    if (show && product != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar el producto: ${product.name}?", style = MaterialTheme.typography.h6)
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
