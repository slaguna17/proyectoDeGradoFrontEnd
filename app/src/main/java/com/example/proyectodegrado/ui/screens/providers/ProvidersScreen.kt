package com.example.proyectodegrado.ui.screens.providers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.ui.components.RefreshableContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    navController: NavController,
    viewModel: ProvidersViewModel
) {
    val providers by viewModel.providers.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsState()
    val selectedProductIds by viewModel.selectedProductIds.collectAsState()
    val isSavingProducts by viewModel.isSavingProducts.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showManageProductsDialog by remember { mutableStateOf(false) }

    var providerToEdit by remember { mutableStateOf<Provider?>(null) }
    var providerToDelete by remember { mutableStateOf<Provider?>(null) }
    var providerForProducts by remember { mutableStateOf<Provider?>(null) }

    var formName by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formEmail by remember { mutableStateOf("") }
    var formPhone by remember { mutableStateOf("") }
    var formContact by remember { mutableStateOf("") }
    var formNotes by remember { mutableStateOf("") }

    var isRefreshing by remember { mutableStateOf(false) }

    fun refreshProviders() {
        isRefreshing = true
        viewModel.fetchProviders(
            onSuccess = { isRefreshing = false },
            onError = { err ->
                errorMessage = err
                isRefreshing = false
            }
        )
    }

    LaunchedEffect(Unit) {
        refreshProviders()
    }

    LaunchedEffect(errorMessage) {
        val msg = errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        errorMessage = null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    formName = ""
                    formAddress = ""
                    formEmail = ""
                    formPhone = ""
                    formContact = ""
                    formNotes = ""
                    showCreateDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Proveedor")
            }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshProviders() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (providers.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay proveedores.",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(providers) { provider ->
                        ProviderItem(
                            provider = provider,
                            onEdit = {
                                providerToEdit = it
                                showEditDialog = true
                            },
                            onDelete = {
                                providerToDelete = it
                                showDeleteDialog = true
                            },
                            onManageProducts = {
                                providerForProducts = it
                                viewModel.loadProductsForProvider(
                                    providerId = it.id,
                                    onError = { err -> errorMessage = err }
                                )
                                showManageProductsDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProviderDialog(
            show = true,
            name = formName,
            onNameChange = { formName = it },
            address = formAddress,
            onAddressChange = { formAddress = it },
            email = formEmail,
            onEmailChange = { formEmail = it },
            phone = formPhone,
            onPhoneChange = { formPhone = it },
            contactPerson = formContact,
            onContactPersonChange = { formContact = it },
            notes = formNotes,
            onNotesChange = { formNotes = it },
            onDismiss = { showCreateDialog = false },
            onSubmit = {
                viewModel.createProvider(
                    ProviderRequest(
                        name = formName,
                        address = formAddress,
                        email = formEmail,
                        phone = formPhone,
                        contactPersonName = formContact,
                        notes = formNotes
                    ),
                    onSuccess = { refreshProviders() },
                    onError = { errorMessage = it }
                )
                showCreateDialog = false
            }
        )
    }

    if (showEditDialog && providerToEdit != null) {
        val currentProvider = providerToEdit!!

        EditProviderDialog(
            show = true,
            name = currentProvider.name,
            onNameChange = { providerToEdit = currentProvider.copy(name = it) },
            address = currentProvider.address,
            onAddressChange = { providerToEdit = currentProvider.copy(address = it) },
            email = currentProvider.email,
            onEmailChange = { providerToEdit = currentProvider.copy(email = it) },
            phone = currentProvider.phone,
            onPhoneChange = { providerToEdit = currentProvider.copy(phone = it) },
            contactPerson = currentProvider.contactPersonName,
            onContactPersonChange = { providerToEdit = currentProvider.copy(contactPersonName = it) },
            notes = currentProvider.notes,
            onNotesChange = { providerToEdit = currentProvider.copy(notes = it) },
            onDismiss = { showEditDialog = false },
            onSubmit = {
                val updatedProvider = providerToEdit ?: return@EditProviderDialog

                viewModel.updateProvider(
                    id = updatedProvider.id,
                    request = ProviderRequest(
                        name = updatedProvider.name,
                        address = updatedProvider.address,
                        email = updatedProvider.email,
                        phone = updatedProvider.phone,
                        contactPersonName = updatedProvider.contactPersonName,
                        notes = updatedProvider.notes
                    ),
                    onSuccess = { refreshProviders() },
                    onError = { errorMessage = it }
                )
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && providerToDelete != null) {
        DeleteProviderDialog(
            show = true,
            message = "¿Seguro que deseas eliminar el proveedor \"${providerToDelete!!.name}\"?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteProvider(
                    id = providerToDelete!!.id,
                    onSuccess = { refreshProviders() },
                    onError = { errorMessage = it }
                )
                showDeleteDialog = false
            }
        )
    }

    if (showManageProductsDialog && providerForProducts != null) {
        ManageProviderProductsDialog(
            show = true,
            providerName = providerForProducts!!.name,
            products = allProducts,
            selectedProductIds = selectedProductIds,
            isSaving = isSavingProducts,
            onToggleProduct = { productId ->
                viewModel.toggleProductSelection(productId)
            },
            onDismiss = {
                showManageProductsDialog = false
                viewModel.clearSelectedProducts()
            },
            onSave = {
                viewModel.syncProviderProducts(
                    providerId = providerForProducts!!.id,
                    onSuccess = {
                        showManageProductsDialog = false
                        viewModel.clearSelectedProducts()
                        refreshProviders()
                    },
                    onError = { errorMessage = it }
                )
            }
        )
    }
}