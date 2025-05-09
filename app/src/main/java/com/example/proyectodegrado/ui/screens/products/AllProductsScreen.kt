package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.di.AppPreferences

@Composable
fun AllProductsScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val createFormState by viewModel.createProductFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val storeId = remember { AppPreferences(context).getStoreId()?.toIntOrNull() ?: 0 }

    val categoryId = 1

    val refreshAllProducts: () -> Unit = {
        viewModel.fetchAllProducts(
            onSuccess = { products = viewModel.products.value },
            onError = { errorMessage = it }
        )
    }

    LaunchedEffect(Unit) {
        refreshAllProducts()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Todos los productos", fontSize = 24.sp)
        Button(
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = {
                viewModel.updateCreateProductFormState(CreateProductFormState(categoryId = categoryId))
                showCreateDialog = true
            },
            enabled = storeId != 0
        ) {
            Text("Crear Producto")
        }
        Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) {
            if (errorMessage == null) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            else Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(products) { prod ->
                    ProductItem(
                        product = prod,
                        onEdit = { /* TODO */ },
                        onDelete = {
                            productToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProductDialog(
            show = true,
            onDismiss = { showCreateDialog = false },
            formState = createFormState,
            imageUploadState = imageUploadState,
            onFormStateChange = viewModel::updateCreateProductFormState,
            onImageUriSelected = viewModel::handleProductImageSelection,
            onCreateClick = { catId, store -> viewModel.createProductFromState(catId, store, {}, {}) }
        )
    }

    if (showDeleteDialog) {
        DeleteProductDialog(
            show = true,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                productToDelete?.let {
                    viewModel.deleteProduct(
                        id = it.id,
                        categoryId = categoryId,
                        onSuccess = { showDeleteDialog = false },
                        onError = { /* TODO */ }
                    )
                }
            },
            product = productToDelete
        )
    }
}
