package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.di.AppPreferences

@Composable
fun ProductsByCategoryScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    categoryId: Int
) {
    val products by viewModel.productsByCategory.collectAsStateWithLifecycle()
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val createFormState by viewModel.createProductFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val storeId = remember { AppPreferences(context).getStoreId()?.toIntOrNull() ?: 0 }

    LaunchedEffect(categoryId) {
        viewModel.fetchProductsByCategory(
            categoryId = categoryId,
            onError = { errorMessage = it }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                viewModel.updateCreateProductFormState(CreateProductFormState(categoryId = categoryId))
                showCreateDialog = true
            },
            enabled = storeId != 0
        ) {
            Text("Crear en categoría")
        }
        Spacer(Modifier.height(8.dp))
        if (products.isEmpty()) {
            if (errorMessage == null) Text("No hay productos", color = MaterialTheme.colorScheme.onBackground)
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
            onCreateClick = { cat, store -> viewModel.createProductFromState(cat, store, {}, {}) }
        )
    }
    if (showDeleteDialog) {
        DeleteProductDialog(
            show = true,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                productToDelete?.let {
                    viewModel.deleteProduct(it.id, categoryId, {}, {})
                }
            },
            product = productToDelete
        )
    }
}
