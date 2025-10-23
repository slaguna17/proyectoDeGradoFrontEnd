package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.ui.components.StoreDropdown
import com.example.proyectodegrado.ui.components.RefreshableContainer
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WorkersScreen(
    viewModel: WorkersViewModel,
    navController: NavController
) {
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val stores by viewModel.stores.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val selectedWorkerContext by viewModel.selectedWorkerContext.collectAsStateWithLifecycle()
    val assignError by viewModel.assignError.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    // Estados para editar/eliminar
    var workerToEdit by remember { mutableStateOf<com.example.proyectodegrado.data.model.Worker?>(null) }
    var workerToDelete by remember { mutableStateOf<com.example.proyectodegrado.data.model.Worker?>(null) }
    var selectedStoreId by remember { mutableStateOf<Int?>(null) }

    // Carga inicial (catálogos + lista)
    LaunchedEffect(Unit) { viewModel.refreshAll() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("registerEmployee") },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.PersonAdd, contentDescription = "Agregar empleado") }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = loading,
            onRefresh = { viewModel.refreshAll(selectedStoreId) },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                StoreDropdown(
                    stores = stores,
                    selectedStoreId = selectedStoreId,
                    onStoreSelected = {
                        selectedStoreId = it
                        viewModel.filterByStore(it)
                    }
                )
                Spacer(Modifier.height(12.dp))

                if (employees.isEmpty() && !loading) {
                    Text("No hay empleados en esta tienda.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(employees, key = { it.id }) { worker ->
                            WorkerItem(
                                worker = worker,
                                onAssignClick = { viewModel.openAssignScheduleDialog(worker) },
                                onEditClick = { workerToEdit = worker },
                                onDeleteClick = { workerToDelete = worker }
                            )
                        }
                    }
                }
            }
        }

        // --- Diálogos ---
        if (selectedWorkerContext != null) {
            AssignScheduleDialog(
                stores = stores,
                schedules = schedules,
                formState = selectedWorkerContext!!.formState,
                onFormStateChange = viewModel::updateAssignForm,
                onConfirm = { viewModel.assignSchedule { /* snackbar si quieres */ } },
                onDismiss = { viewModel.closeAssignScheduleDialog() },
                errorMessage = assignError
            )
        }

        workerToEdit?.let { worker ->
            EditWorkerDialog(
                initialName = worker.fullName ?: "",
                initialEmail = worker.email ?: "",
                initialPhone = worker.phone ?: "",
                onConfirm = { name, email, phone ->
                    viewModel.updateWorker(worker.id, name, email, phone) { workerToEdit = null }
                },
                onDismiss = { workerToEdit = null }
            )
        }

        workerToDelete?.let { worker ->
            DeleteWorkerDialog(
                workerName = worker.fullName ?: worker.username,
                onConfirm = {
                    viewModel.deleteWorker(worker.id) { workerToDelete = null }
                },
                onDismiss = { workerToDelete = null }
            )
        }
    }
}
