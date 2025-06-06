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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.ProductRequest2
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState

@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    formState: CreateProductFormState,
    imageUploadState: UploadImageState,
    onFormStateChange: (CreateProductFormState) -> Unit,
    onImageUriSelected: (Uri?) -> Unit,
    onCreateClick: (categoryId: Int, storeId: Int) -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Crear Producto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = { onFormStateChange(formState.copy(name = it)) },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = { onFormStateChange(formState.copy(description = it)) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.sku,
                    onValueChange = { onFormStateChange(formState.copy(sku = it)) },
                    label = { Text("SKU") },
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
                    value = formState.stock,
                    onValueChange = { new ->
                        if (new.all { it.isDigit() }) onFormStateChange(formState.copy(stock = new))
                    },
                    label = { Text("Stock inicial") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.expirationDate,
                    onValueChange = { onFormStateChange(formState.copy(expirationDate = it)) },
                    label = { Text("Fecha expiración (YYYY-MM-DD)") },
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
                    Button(
                        onClick = { onCreateClick(formState.categoryId, /* storeId */ 0) },
                        enabled = imageUploadState is UploadImageState.Idle || imageUploadState is UploadImageState.Error
                    ) {
                        Text("Crear")
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
    onEdit: (ProductRequest2) -> Unit,
    productRequest: ProductRequest2?
) {
    // ... idéntico a tu implementación, con paquete ajustado ...
}

@Composable
fun DeleteProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    product: com.example.proyectodegrado.data.model.Product?
) {
    // ... idéntico a tu implementación, con paquete ajustado ...
}
