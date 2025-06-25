package com.example.proyectodegrado.ui.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun StoreScreen(
    navController: NavController,
    viewModel: StoreViewModel
) {
    val stores      by viewModel.stores.collectAsStateWithLifecycle()
    val context     = androidx.compose.ui.platform.LocalContext.current
    val preferences = remember { AppPreferences(context) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStoreId by rememberSaveable { mutableStateOf(preferences.getStoreId() ?: "") }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog   by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var storeToEdit      by remember { mutableStateOf<Store?>(null) }
    var storeToDelete    by remember { mutableStateOf<Store?>(null) }

    // Campos del formulario de crear
    var formName    by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formCity    by remember { mutableStateOf("") }
    var formLogo    by remember { mutableStateOf("") }
    var formHistory by remember { mutableStateOf("") }
    var formPhone   by remember { mutableStateOf("") }

    // Campos temporales para editar (se cargan del store a editar)
    var editName    by remember { mutableStateOf("") }
    var editAddress by remember { mutableStateOf("") }
    var editCity    by remember { mutableStateOf("") }
    var editLogo    by remember { mutableStateOf("") }
    var editHistory by remember { mutableStateOf("") }
    var editPhone   by remember { mutableStateOf("") }

    // Para Swipe Refresh
    var isRefreshing by remember { mutableStateOf(false) }

    // Carga inicial y refresh
    fun refreshStores() {
        isRefreshing = true
        viewModel.fetchStores(
            onSuccess = { isRefreshing = false },
            onError = { err -> errorMessage = err; isRefreshing = false }
        )
    }

    LaunchedEffect(Unit) {
        refreshStores()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar tienda seleccionada (opcional)
        if (selectedStoreId.isNotBlank()) {
            Text(
                text = "Tienda seleccionada: $selectedStoreId",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Button(
            onClick = {
                // Limpiar campos al abrir diálogo de crear
                formName = ""
                formAddress = ""
                formCity = ""
                formLogo = ""
                formHistory = ""
                formPhone = ""
                showCreateDialog = true
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("+ Nueva Tienda")
        }

        Spacer(Modifier.height(4.dp))

        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshStores() },
            modifier = Modifier.weight(1f)
        ) {
            if (stores.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Cargando tiendas...", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
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
                                // Al editar, cargar datos del store seleccionado
                                editName = it.name
                                editAddress = it.address
                                editCity = it.city
                                editLogo = it.logo
                                editHistory = it.history
                                editPhone = it.phone
                                showEditDialog = true
                            },
                            onDelete = { storeToDelete = it; showDeleteDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Diálogo crear tienda
    if (showCreateDialog) {
        CreateStoreDialog(
            show = true,
            name = formName,
            onNameChange = { formName = it },
            address = formAddress,
            onAddressChange = { formAddress = it },
            city = formCity,
            onCityChange = { formCity = it },
            logo = formLogo,
            onLogoChange = { formLogo = it },
            history = formHistory,
            onHistoryChange = { formHistory = it },
            phone = formPhone,
            onPhoneChange = { formPhone = it },
            onDismiss = { showCreateDialog = false },
            onSubmit = {
                viewModel.createStore(
                    StoreRequest(formName, formAddress, formCity, formLogo, formHistory, formPhone),
                    onSuccess = { refreshStores() },
                    onError = { errorMessage = it }
                )
                showCreateDialog = false
            }
        )
    }

    // Diálogo editar tienda
    if (showEditDialog && storeToEdit != null) {
        EditStoreDialog(
            show = true,
            name = editName,
            onNameChange = { editName = it },
            address = editAddress,
            onAddressChange = { editAddress = it },
            city = editCity,
            onCityChange = { editCity = it },
            logo = editLogo,
            onLogoChange = { editLogo = it },
            history = editHistory,
            onHistoryChange = { editHistory = it },
            phone = editPhone,
            onPhoneChange = { editPhone = it },
            onDismiss = { showEditDialog = false },
            onSubmit = {
                viewModel.updateStore(
                    id = storeToEdit!!.id,
                    StoreRequest(editName, editAddress, editCity, editLogo, editHistory, editPhone),
                    onSuccess = { refreshStores() },
                    onError = { errorMessage = it }
                )
                showEditDialog = false
            }
        )
    }

    // Diálogo eliminar tienda
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
