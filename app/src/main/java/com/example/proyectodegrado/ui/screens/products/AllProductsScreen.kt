package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.RefreshableContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    val allProducts by viewModel.products.collectAsStateWithLifecycle()
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

    val refreshAllProducts: () -> Unit = {
        isRefreshing = true
        errorMessage = null
        viewModel.fetchAllProducts(
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

    LaunchedEffect(Unit) {
        if (allProducts.isEmpty()) {
            refreshAllProducts()
        } else {
            isLoadingFirstTime = false
        }
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
                    viewModel.resetCreateProductFormState()
                    showCreateDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { innerPadding ->

        if (storeId == null) {
            Text(
                "Selecciona una tienda para poder gestionar productos.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            RefreshableContainer(
                refreshing = isRefreshing,
                onRefresh = refreshAllProducts,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    isLoadingFirstTime -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    allProducts.isNotEmpty() -> {
                        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                            items(allProducts, key = { it.id }) { product ->
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
                        Text("No hay productos.", color = MaterialTheme.colorScheme.onBackground)
                    }
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
                            refreshAllProducts()
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
                            refreshAllProducts()
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
                            refreshAllProducts()
                        },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }
}