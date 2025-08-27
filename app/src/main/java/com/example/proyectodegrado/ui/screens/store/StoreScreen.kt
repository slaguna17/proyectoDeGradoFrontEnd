package com.example.proyectodegrado.ui.screens.store

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.RefreshableContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    navController: NavController,
    viewModel: StoreViewModel
) {
    val stores            by viewModel.stores.collectAsStateWithLifecycle()
    val imageUploadState  by viewModel.imageUploadUiState.collectAsStateWithLifecycle()
    val createLogoKey     by viewModel.createLogoKey.collectAsStateWithLifecycle()
    val editLogoKey       by viewModel.editLogoKey.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current
    val preferences = remember { AppPreferences(context) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStoreId by rememberSaveable { mutableStateOf(preferences.getStoreId() ?: "") }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var storeToEdit by remember { mutableStateOf<Store?>(null) }
    var storeToDelete by remember { mutableStateOf<Store?>(null) }

    // Campos del formulario crear
    var formName by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formCity by remember { mutableStateOf("") }
    var formLogo by remember { mutableStateOf("") } // preview de URL externa (opcional)
    var formHistory by remember { mutableStateOf("") }
    var formPhone by remember { mutableStateOf("") }

    // Campos editar
    var editName by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editLogo by remember { mutableStateOf("") } // preview de URL externa actual
    var editHistory by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }

    var isRefreshing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun refreshStores() {
        isRefreshing = true
        viewModel.fetchStores(
            onSuccess = { isRefreshing = false },
            onError = { err -> errorMessage = err; isRefreshing = false }
        )
    }

    LaunchedEffect(Unit) { refreshStores() }
    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it); errorMessage = null }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                formName = ""; formAddress = ""; formCity = ""
                formLogo = ""; formHistory = ""; formPhone = ""
                viewModel.clearCreateLogoKey()
                showCreateDialog = true
            }) { Icon(Icons.Default.Add, contentDescription = "Nueva Tienda") }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshStores() },
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            if (stores.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "Cargando tiendas...",
                        color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                ) {
                    items(stores) { store ->
                        StoreItem(
                            store = store,
                            isSelected = selectedStoreId == store.id.toString(),
                            onSelect = {
                                selectedStoreId = store.id.toString()
                                preferences.saveStoreId(selectedStoreId)
                            },
                            onEdit = {
                                storeToEdit = it
                                editName = it.name
                                editAddress = it.address
                                editCity = it.city
                                editLogo = it.logoUrl ?: it.logo ?: "" // preview actual
                                editHistory = it.history
                                editPhone = it.phone
                                viewModel.clearEditLogoKey()
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

    if (showCreateDialog) {
        CreateStoreDialog(
            show = true,
            name = formName, onNameChange = { formName = it },
            address = formAddress, onAddressChange = { formAddress = it },
            city = formCity, onCityChange = { formCity = it },
            logo = formLogo, onLogoChange = { formLogo = it },
            history = formHistory, onHistoryChange = { formHistory = it },
            phone = formPhone, onPhoneChange = { formPhone = it },
            uploadState = imageUploadState,
            onPickLogo = { uri -> viewModel.handleStoreLogoSelection(uri) },
            onDismiss = { showCreateDialog = false },
            onSubmit = {
                // Enviamos logo_key si existe; si no, logo (URL externa)
                viewModel.createStore(
                    StoreRequest(
                        name = formName,
                        address = formAddress,
                        city = formCity,
                        logoKey = createLogoKey,   // <- KEY S3 si subiste imagen
                        logo = formLogo.ifBlank { null }, // <- URL externa opcional
                        history = formHistory,
                        phone = formPhone
                    ),
                    onSuccess = { refreshStores() },
                    onError = { errorMessage = it }
                )
                showCreateDialog = false
            }
        )
    }

    if (showEditDialog && storeToEdit != null) {
        EditStoreDialog(
            show = true,
            name = editName, onNameChange = { editName = it },
            address = editAddress, onAddressChange = { editAddress = it },
            city = editCity, onCityChange = { editCity = it },
            logo = editLogo, onLogoChange = { editLogo = it },
            history = editHistory, onHistoryChange = { editHistory = it },
            phone = editPhone, onPhoneChange = { editPhone = it },
            uploadState = imageUploadState,
            onPickLogo = { uri -> viewModel.selectLogoForEdit(storeToEdit!!.id, uri) },
            onDismiss = { showEditDialog = false },
            onSubmit = {
                viewModel.updateStore(
                    id = storeToEdit!!.id,
                    request = StoreRequest(
                        name = editName,
                        address = editAddress,
                        city = editCity,
                        logoKey = editLogoKey,            // <- KEY S3 si se cambió
                        logo = editLogo.ifBlank { null }, // <- URL externa opcional
                        history = editHistory,
                        phone = editPhone
                    ),
                    onSuccess = { refreshStores() },
                    onError = { errorMessage = it }
                )
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && storeToDelete != null) {
        DeleteStoreDialog(
            show = true,
            message = "¿Seguro que deseas eliminar la tienda \"${storeToDelete!!.name}\"?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteStore(
                    id = storeToDelete!!.id,
                    onSuccess = { refreshStores() },
                    onError = { errorMessage = it }
                )
                showDeleteDialog = false
            }
        )
    }
}
