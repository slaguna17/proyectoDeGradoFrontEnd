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
    var showAssignDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveAssignmentDialog by remember { mutableStateOf(false) }
    var showAdjustStockDialog by remember { mutableStateOf(false) }
    var assignmentToRemove by remember { mutableStateOf<Pair<Product, StoreOption>?>(null) }

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

    val refreshProducts: () -> Unit = {
        isRefreshing = true
        errorMessage = null
        val onErrorCallback: (String) -> Unit = { errorMsg ->
            errorMessage = errorMsg
            isRefreshing = false
            isLoadingFirstTime = false
        }
        val onSuccessCallback: () -> Unit = {
            isRefreshing = false
            isLoadingFirstTime = false
        }

        if (selectedStoreId != null) {
            viewModel.fetchProductsByStore(selectedStoreId!!, onSuccessCallback, onErrorCallback)
        } else {
            viewModel.fetchAllProducts(onSuccessCallback, onErrorCallback)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchStores(onError = { msg -> errorMessage = msg })
        if (availableCategories.isEmpty()) viewModel.fetchAvailableCategories()
    }

    LaunchedEffect(currentStoreForCrud) {
        if (selectedStoreId == null && currentStoreForCrud != null) {
            viewModel.setSelectedStore(currentStoreForCrud)
        }
    }

    LaunchedEffect(selectedStoreId) {
        refreshProducts()
    }

    LaunchedEffect(Unit) {
        if (availableCategories.isEmpty()) viewModel.fetchAvailableCategories()
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
                FloatingActionButton(
                    onClick = {
                        viewModel.resetForm()
                        showCreateDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StoreFilterBar(
                stores = storeOptions,
                selectedStoreId = selectedStoreId,
                onStoreSelected = { id ->
                    viewModel.setSelectedStore(id)
                }
            )

            RefreshableContainer(
                refreshing = isRefreshing,
                onRefresh = refreshProducts,
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
                                    currentStoreId = selectedStoreId,
                                    allStores = storeOptions,
                                    onEdit = {
                                        productToInteractWith = it
                                        viewModel.prepareFormForEdit(it, selectedStoreId)
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        productToInteractWith = it
                                        showDeleteDialog = true
                                    },
                                    onAssignToStore = {
                                        productToInteractWith = it
                                        showAssignDialog = true
                                    },
                                    onRemoveFromStore = { prod, storeId ->
                                        val store = storeOptions.find { it.id == storeId }
                                        if (store != null) {
                                            assignmentToRemove = Pair(prod, store)
                                            showRemoveAssignmentDialog = true
                                        }
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
            onPurchasePriceChange = viewModel::onPurchasePriceChange,
            onSalePriceChange = viewModel::onSalePriceChange,
            onCreateClick = {
                if (currentStoreForCrud != null) {
                    viewModel.createProduct(
                        storeId = currentStoreForCrud,
                        onSuccess = { showCreateDialog = false; refreshProducts() },
                        onError = { errMsg -> errorMessage = errMsg }
                    )
                }
            }
        )
    }

    if (showAssignDialog && productToInteractWith != null) {
        val assignedStoreIds = productToInteractWith?.stores?.map { it.id }?.toSet() ?: emptySet()
        val availableStoresForAssignment = storeOptions.filterNot { it.id in assignedStoreIds }

        AssignProductDialog(
            show = true,
            onDismiss = { showAssignDialog = false },
            productName = productToInteractWith!!.name,
            availableStores = availableStoresForAssignment,
            onAssign = { storeId, stock ->
                viewModel.addProductToStore(
                    productId = productToInteractWith!!.id,
                    storeId = storeId,
                    stock = stock.toIntOrNull() ?: 0,
                    onSuccess = {
                        showAssignDialog = false
                        refreshProducts()
                    },
                    onError = { errMsg ->
                        errorMessage = errMsg
                    }
                )
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
            onPurchasePriceChange = viewModel::onPurchasePriceChange,
            onSalePriceChange = viewModel::onSalePriceChange,
            onAdjustStockClick = {
                showEditDialog = false
                showAdjustStockDialog = true
            },
            onEditClick = {
                if (currentStoreForCrud != null) {
                    viewModel.updateProduct(
                        id = productToInteractWith!!.id,
                        storeId = currentStoreForCrud,
                        onSuccess = { showEditDialog = false; refreshProducts() },
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

    if (showRemoveAssignmentDialog && assignmentToRemove != null) {
        RemoveAssignmentDialog(
            show = true,
            onDismiss = { showRemoveAssignmentDialog = false },
            productName = assignmentToRemove!!.first.name,
            storeName = assignmentToRemove!!.second.name,
            onConfirm = {
                viewModel.removeProductFromStore(
                    productId = assignmentToRemove!!.first.id,
                    storeId = assignmentToRemove!!.second.id,
                    onSuccess = { refreshProducts() },
                    onError = { errMsg -> errorMessage = errMsg }
                )
            }
        )
    }

    if (showAdjustStockDialog && productToInteractWith != null && currentStoreForCrud != null) {
        AdjustStockDialog(
            show = true,
            onDismiss = { showAdjustStockDialog = false },
            productName = productToInteractWith!!.name,
            currentStock = formState.stock,
            onConfirm = { newStock ->
                viewModel.addProductToStore(
                    productId = productToInteractWith!!.id,
                    storeId = currentStoreForCrud,
                    stock = newStock.toIntOrNull() ?: 0,
                    onSuccess = {
                        showAdjustStockDialog = false
                        refreshProducts()
                    },
                    onError = { errMsg -> errorMessage = errMsg }
                )
            }
        )
    }
}
