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
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()
    val availableCategories by viewModel.availableCategories.collectAsStateWithLifecycle()
    val storeOptions by viewModel.stores.collectAsStateWithLifecycle()
    val selectedStoreId by viewModel.selectedStoreId.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingFirstTime by remember { mutableStateOf(true) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var productToInteractWith by remember { mutableStateOf<Product?>(null) }

    val context = LocalContext.current
    val currentStoreForCrud = remember { AppPreferences(context).getStoreId()?.toIntOrNull() }

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

    // Cargar tiendas al entrar
    LaunchedEffect(Unit) {
        viewModel.fetchStores(onError = { msg -> errorMessage = msg })
    }

    // Usar la tienda de preferencias como preseleccionada si existe
    LaunchedEffect(currentStoreForCrud) {
        if (selectedStoreId == null && currentStoreForCrud != null) {
            viewModel.setSelectedStore(currentStoreForCrud)
        }
    }

    // Cargar productos iniciales o cuando cambia la tienda seleccionada
    LaunchedEffect(selectedStoreId) {
        if (allProducts.isEmpty()) {
            refreshAllProducts()
        } else {
            // Si ya había datos, solo refrescamos por cambio de tienda
            refreshAllProducts()
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
            if (currentStoreForCrud != null) {
                FloatingActionButton(onClick = {
                    viewModel.resetForm()
                    showCreateDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // --- NUEVO: Filtro por tienda ---
            StoreFilterBar(
                stores = storeOptions,
                selectedStoreId = selectedStoreId,
                onStoreSelected = { id ->
                    viewModel.setSelectedStore(id)
                }
            )

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
                                    currentStoreId = selectedStoreId, // NUEVO: stock por tienda
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
            show = true, onDismiss = { showCreateDialog = false; viewModel.resetForm() },
            formState = formState, imageUploadState = imageUploadState,
            availableCategories = availableCategories, onNameChange = viewModel::onNameChange,
            onSkuChange = viewModel::onSkuChange, onDescriptionChange = viewModel::onDescriptionChange,
            onBrandChange = viewModel::onBrandChange, onCategorySelected = viewModel::onCategorySelected,
            onStockChange = viewModel::onStockChange, onImageSelected = viewModel::onImageSelected,
            onCreateClick = {
                if (currentStoreForCrud != null) {
                    viewModel.createProduct(
                        storeId = currentStoreForCrud,
                        onSuccess = { showCreateDialog = false; refreshAllProducts() },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }

    if (showEditDialog && productToInteractWith != null) {
        EditProductDialog(
            show = true, onDismiss = { showEditDialog = false },
            formState = formState, imageUploadState = imageUploadState,
            availableCategories = availableCategories, onNameChange = viewModel::onNameChange,
            onSkuChange = viewModel::onSkuChange, onDescriptionChange = viewModel::onDescriptionChange,
            onBrandChange = viewModel::onBrandChange, onCategorySelected = viewModel::onCategorySelected,
            onStockChange = viewModel::onStockChange, onImageSelected = viewModel::onImageSelected,
            onEditClick = {
                if (currentStoreForCrud != null) {
                    viewModel.updateProduct(
                        id = productToInteractWith!!.id,
                        storeId = currentStoreForCrud,
                        onSuccess = { showEditDialog = false; refreshAllProducts() },
                        onError = { /* Manejar error */ }
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
