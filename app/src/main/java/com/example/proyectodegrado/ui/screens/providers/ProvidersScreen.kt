package com.example.proyectodegrado.ui.screens.providers

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
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.ui.components.RefreshableContainer

@Composable
fun ProvidersScreen(
    navController: NavController,
    viewModel: ProvidersViewModel
) {
    val providers by viewModel.providers.collectAsStateWithLifecycle()
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog   by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var providerToEdit   by remember { mutableStateOf<Provider?>(null) }
    var providerToDelete by remember { mutableStateOf<Provider?>(null) }

    // Formulario para crear proveedor
    var formName    by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formEmail   by remember { mutableStateOf("") }
    var formPhone   by remember { mutableStateOf("") }
    var formContact by remember { mutableStateOf("") }
    var formNotes   by remember { mutableStateOf("") }

    // Para Swipe Refresh
    var isRefreshing by remember { mutableStateOf(false) }

    // Carga inicial y refresh
    fun refreshProviders() {
        isRefreshing = true
        viewModel.fetchProviders(
            onSuccess = { isRefreshing = false },
            onError = { err -> errorMessage = err; isRefreshing = false }
        )
    }

    LaunchedEffect(Unit) {
        refreshProviders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                formName = ""
                formAddress = ""
                formEmail = ""
                formPhone = ""
                formContact = ""
                formNotes = ""
                showCreateDialog = true
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text("+ Crear Proveedor")
        }

        Spacer(Modifier.height(4.dp))

        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshProviders() },
            modifier = Modifier.weight(1f)
        ) {
            if (providers.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Cargando proveedores...", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
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
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo crear proveedor
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
                    ProviderRequest(formName, formAddress, formEmail, formPhone, formContact, formNotes),
                    onSuccess = { refreshProviders() },
                    onError = { errorMessage = it }
                )
                showCreateDialog = false
            }
        )
    }

    // Diálogo editar proveedor
    if (showEditDialog && providerToEdit != null) {
        val p = providerToEdit!!
        EditProviderDialog(
            show = true,
            name = p.name,
            onNameChange = { providerToEdit = p.copy(name = it) },
            address = p.address,
            onAddressChange = { providerToEdit = p.copy(address = it) },
            email = p.email,
            onEmailChange = { providerToEdit = p.copy(email = it) },
            phone = p.phone,
            onPhoneChange = { providerToEdit = p.copy(phone = it) },
            contactPerson = p.contact_person_name,
            onContactPersonChange = { providerToEdit = p.copy(contact_person_name = it) },
            notes = p.notes,
            onNotesChange = { providerToEdit = p.copy(notes = it) },
            onDismiss = { showEditDialog = false },
            onSubmit = {
                viewModel.updateProvider(
                    id = p.id,
                    ProviderRequest(p.name, p.address, p.email, p.phone, p.contact_person_name, p.notes),
                    onSuccess = { refreshProviders() },
                    onError = { errorMessage = it }
                )
                showEditDialog = false
            }
        )
    }

    // Diálogo eliminar proveedor
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
}
