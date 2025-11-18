package com.example.proyectodegrado.ui.screens.store

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.RefreshableContainer
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    navController: NavController,
    viewModel: StoreViewModel
) {
    // Estado proveniente del VM
    val stores by viewModel.stores.collectAsStateWithLifecycle()
    val form by viewModel.formState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val preferences = remember { AppPreferences(context) }
    var selectedStoreId by rememberSaveable { mutableStateOf(preferences.getStoreId() ?: "") }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var storeToEdit by remember { mutableStateOf<Store?>(null) }
    var storeToDelete by remember { mutableStateOf<Store?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var initialLoadError by remember { mutableStateOf<String?>(null) }

    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun refreshStores(isInitialLoad: Boolean = false) {
        isRefreshing = true
        if (isInitialLoad) initialLoadError = null

        viewModel.fetchStores(
            onSuccess = { isRefreshing = false },
            onError = { err ->
                if (isInitialLoad && stores.isEmpty()) {
                    initialLoadError = err
                }
                showSnackbar(err)
                isRefreshing = false
            }
        )
    }

    LaunchedEffect(Unit) {
        refreshStores(isInitialLoad = true)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm()
                    showCreateDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ){
                Icon(Icons.Default.Add, contentDescription = "Nueva Tienda")
            }
        }
    ) {
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshStores() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (stores.isEmpty() && isRefreshing) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (stores.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = initialLoadError ?: "No hay tiendas. ¡Agrega una nueva!",
                        color = if (initialLoadError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(stores) { store ->
                        StoreItem(
                            store = store,
                            isSelected = selectedStoreId == store.id.toString(),
                            onSelect = {
                                selectedStoreId = store.id.toString()
                                preferences.saveStoreId(selectedStoreId)
                                preferences.saveStoreName(store.name)
                            },
                            onEdit = {
                                viewModel.loadStoreForEdit(it)
                                storeToEdit = it
                                showEditDialog = true
                            },
                            onDelete = {
                                storeToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // --- Create ---
    if (showCreateDialog) {
        CreateStoreDialog(
            show = true,
            name = form.name, onNameChange = viewModel::onNameChange,
            address = form.address, onAddressChange = viewModel::onAddressChange,
            city = form.city, onCityChange = viewModel::onCityChange,
            logo = form.localLogoUri?.toString() ?: "",
            onLogoChange = { /* No-op, handled by onPickLogo */ },
            history = form.history, onHistoryChange = viewModel::onHistoryChange,
            phone = form.phone, onPhoneChange = viewModel::onPhoneChange,
            uploadState = imageUploadState,
            onPickLogo = viewModel::onPickLogo,
            onDismiss = { showCreateDialog = false },
            onSubmit = {
                viewModel.createStore(
                    onSuccess = { showCreateDialog = false; refreshStores() },
                    onError = { err -> showSnackbar(err) }
                )
            }
        )
    }

    // --- Edit ---
    if (showEditDialog && storeToEdit != null) {
        EditStoreDialog(
            show = true,
            name = form.name, onNameChange = viewModel::onNameChange,
            address = form.address, onAddressChange = viewModel::onAddressChange,
            city = form.city, onCityChange = viewModel::onCityChange,
            logo = form.localLogoUri?.toString() ?: (storeToEdit?.logoUrl ?: storeToEdit?.logo ?: ""),
            onLogoChange = { },
            history = form.history, onHistoryChange = viewModel::onHistoryChange,
            phone = form.phone, onPhoneChange = viewModel::onPhoneChange,
            uploadState = imageUploadState,
            onPickLogo = viewModel::onPickLogo,
            onDismiss = { showEditDialog = false },
            onSubmit = {
                viewModel.updateStore(
                    id = storeToEdit!!.id,
                    onSuccess = {
                        showEditDialog = false
                        // No es necesario llamar a refreshStores aquí, el VM ya lo hace.
                    },
                    onError = { err -> showSnackbar(err) }
                )
            }
        )
    }

    // --- Delete ---
    if (showDeleteDialog && storeToDelete != null) {
        DeleteStoreDialog(
            show = true,
            message = "¿Seguro que deseas eliminar la tienda \"${storeToDelete!!.name}\"?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteStore(
                    id = storeToDelete!!.id,
                    onSuccess = {
                        showDeleteDialog = false
                        // No es necesario llamar a refreshStores aquí, el VM ya lo hace.
                    },
                    // ✨ FIX 5: Usamos showSnackbar para reportar el error.
                    onError = { err -> showSnackbar(err) }
                )
            }
        )
    }
}