package com.example.proyectodegrado.ui.screens.role

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleScreen(
    navController: NavController,
    viewModel: RoleViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var roleToDelete by remember { mutableStateOf<Role?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Rol")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.roles, key = { it.id }) { role ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.openDialog(role) }
                        ) {
                            ListItem(
                                headlineContent = { Text(role.name, style = MaterialTheme.typography.titleMedium) },
                                supportingContent = { Text(role.description) },
                                trailingContent = {
                                    IconButton(onClick = { roleToDelete = role }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Rol")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    CreateEditRoleDialog(
        show = uiState.isDialogShown,
        onDismiss = viewModel::closeDialog,
        onSave = viewModel::saveRole,
        role = uiState.currentRoleInDialog,
        allPermits = uiState.allPermits,
        selectedPermitIds = uiState.selectedPermitIdsInDialog,
        onPermitCheckedChange = viewModel::onPermitCheckedChange
    )

    DeleteRoleDialog(
        show = roleToDelete != null,
        role = roleToDelete,
        onDismiss = { roleToDelete = null },
        onConfirm = {
            roleToDelete?.let { viewModel.deleteRole(it.id) }
            roleToDelete = null
        }
    )
}