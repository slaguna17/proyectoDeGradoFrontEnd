package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.proyectodegrado.ui.components.uploadImage


@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String, Int, Int, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    image: String,
    onImageChange:(String) -> Unit,
    sku: String,
    onSkuChange: (String) -> Unit,
    brand: String,
    onBrandChange: (String) -> Unit,
    category_id: Int,
    stock: Int,
    onStockChange: (String) -> Unit,
    expiration_date:String,
    onDateChange: (String) -> Unit
) {
    // Add a string representation of stock for the text field
    val stockText = remember { mutableStateOf(stock.toString()) }

    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Producto", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre del producto") })
                    OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text("Descripción del producto") })
                    OutlinedTextField(value = sku, onValueChange = onSkuChange, label = { Text("SKU del producto") })
                    OutlinedTextField(value = brand, onValueChange = onBrandChange, label = { Text("Marca del producto") })
                    OutlinedTextField(value = stock.toString(), onValueChange = onStockChange, label = { Text("Stock") })
                    OutlinedTextField(value = expiration_date, onValueChange = onDateChange, label = { Text("Fecha de expiracion") })
                    uploadImage(buttonText = "Elegir foto del producto")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, description,image,sku, brand, category_id, stock, expiration_date); onDismiss() }) { Text("Crear") }
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
                    uploadImage(buttonText = "Cambiar foto del producto")
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
