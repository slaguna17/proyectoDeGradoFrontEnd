package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.RefreshableContainer

@Composable
fun ProductsByCategoryScreen(
    navController: NavController,
    viewModel: ProductViewModel,
    categoryId: Int
) {
    val productsByCategory by viewModel.productsByCategory.collectAsStateWithLifecycle()
    val createFormState by viewModel.createProductFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingFirstTime by remember { mutableStateOf(true) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var productToInteractWith by remember { mutableStateOf<Product?>(null) }

    val context = LocalContext.current
    val storeId = remember { AppPreferences(context).getStoreId()?.toIntOrNull() }

    val refreshProducts: () -> Unit = {
        isRefreshing = true
        errorMessage = null
        viewModel.fetchProductsByCategory(
            categoryId = categoryId,
            onSuccess = {
                isRefreshing = false
                isLoadingFirstTime = false
            },
            onError = { errorMsg ->
                errorMessage = errorMsg
                isRefreshing = false
                isLoadingFirstTime = false
            }
        )
    }

    LaunchedEffect(categoryId) {
        refreshProducts()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (storeId != null) {
                FloatingActionButton(onClick = {
                    viewModel.updateCreateProductFormState(CreateProductFormState(categoryId = categoryId))
                    showCreateDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = refreshProducts,
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoadingFirstTime -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                productsByCategory.isNotEmpty() -> {
                    LazyColumn {
                        items(productsByCategory, key = { it.id }) { product ->
                            ProductItem(
                                product = product,
                                onEdit = {
                                    productToInteractWith = it
                                    viewModel.prepareFormForEdit(it)
                                    showEditDialog = true
                                },
                                onDelete = {
                                    productToInteractWith = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos en esta categoría.", color = MaterialTheme.colorScheme.onBackground)
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
            availableCategories = availableCategories,
            onFormStateChange = viewModel::updateCreateProductFormState,
            onImageUriSelected = viewModel::handleProductImageSelection,
            onCreateClick = {
                if (storeId != null) {
                    viewModel.createProductFromState(
                        storeId = storeId,
                        onSuccess = {
                            showCreateDialog = false
                            refreshProducts()
                        },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }

    if (showEditDialog) {
        EditProductDialog(
            show = true,
            onDismiss = { showEditDialog = false },
            product = productToInteractWith,
            availableCategories = availableCategories,
            onEditClick = { updatedFormState ->
                if (storeId != null && productToInteractWith != null) {
                    viewModel.updateProduct(
                        id = productToInteractWith!!.id,
                        updatedFormState = updatedFormState,
                        storeId = storeId,
                        onSuccess = {
                            showEditDialog = false
                            refreshProducts()
                        },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }

    if (showDeleteDialog) {
        DeleteProductDialog(
            show = true,
            onDismiss = { showDeleteDialog = false },
            product = productToInteractWith,
            onDelete = {
                productToInteractWith?.let {
                    viewModel.deleteProduct(
                        id = it.id,
                        onSuccess = {
                            showDeleteDialog = false
                            refreshProducts()
                        },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }
}
