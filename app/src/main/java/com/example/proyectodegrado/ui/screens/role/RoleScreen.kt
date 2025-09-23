package com.example.proyectodegrado.ui.screens.role

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.ui.components.RefreshableContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleScreen(
    navController: NavController,
    viewModel: RoleViewModel
) {
    val roles by viewModel.roles.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var roleToEdit by remember { mutableStateOf<Role?>(null) }
    var roleToDelete by remember { mutableStateOf<Role?>(null) }

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun refreshData() {
        isRefreshing = true
        viewModel.fetchRoles(
            onSuccess = { isRefreshing = false; isLoading = false },
            onError = { error ->
                isRefreshing = false
                isLoading = false
                showSnackbar(error)
            }
        )
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Turno")
            }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = ::refreshData,
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                roles.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay turnos creados.")
                    }
                }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
                        items(roles, key = { it.id }) { role ->
                            RoleItem(
                                role = role,
                                onEdit = {
                                    roleToEdit = it
                                    showEditDialog = true
                                },
                                onDelete = {
                                    roleToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    CreateRoleDialog(
        show = showCreateDialog,
        onDismiss = { showCreateDialog = false },
        onConfirm = { request ->
            viewModel.createRole(request,
                onSuccess = {
                    showSnackbar("Turno creado")
                    refreshData()
                },
                onError = { error -> showSnackbar(error) }
            )
            showCreateDialog = false
        }
    )

    EditRoleDialog(
        show = showEditDialog,
        role = roleToEdit,
        onDismiss = { showEditDialog = false },
        onConfirm = { request ->
            roleToEdit?.let {
                viewModel.updateRole(it.id, request,
                    onSuccess = {
                        showSnackbar("Turno actualizado")
                        refreshData()
                    },
                    onError = { error -> showSnackbar(error) }
                )
            }
            showEditDialog = false
        }
    )

    DeleteRoleDialog(
        show = showDeleteDialog,
        role = roleToDelete,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            roleToDelete?.let {
                viewModel.deleteRole(it.id,
                    onSuccess = {
                        showSnackbar("Turno eliminado")
                        refreshData()
                    },
                    onError = { error -> showSnackbar(error) }
                )
            }
            showDeleteDialog = false
        }
    )
}
