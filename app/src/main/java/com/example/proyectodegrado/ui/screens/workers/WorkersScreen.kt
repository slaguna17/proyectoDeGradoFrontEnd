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

@Composable
fun WorkersScreen(
    viewModel: WorkersViewModel,
    navController: NavController
) {
    val employees by viewModel.employees.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val selectedWorkerContext by viewModel.selectedWorkerContext.collectAsState()
    val assignError by viewModel.assignError.collectAsState()

    // Estados para editar/eliminar
    var workerToEdit by remember { mutableStateOf<com.example.proyectodegrado.data.model.Worker?>(null) }
    var workerToDelete by remember { mutableStateOf<com.example.proyectodegrado.data.model.Worker?>(null) }
    var selectedStoreId by remember { mutableStateOf<Int?>(null) }

    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.loadStoresAndSchedules()
        viewModel.filterByStore(null)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("registerEmployee") }
            ) { Icon(Icons.Default.PersonAdd, contentDescription = "Agregar empleado") }
        }
    ) { innerPadding ->
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

            if (employees.isEmpty()) {
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

        // --- Dialogos ---
        if (selectedWorkerContext != null) {
            AssignScheduleDialog(
                stores = stores,
                schedules = schedules,
                formState = selectedWorkerContext!!.formState,
                onFormStateChange = viewModel::updateAssignForm,
                onConfirm = { viewModel.assignSchedule { /* Muestra snackbar o mensaje */ } },
                onDismiss = { viewModel.closeAssignScheduleDialog() },
                errorMessage = assignError
            )
        }

        // Editar empleado
        workerToEdit?.let { worker ->
            EditWorkerDialog(
                initialName = worker.fullName ?: "",
                initialEmail = worker.email ?: "",
                initialPhone = worker.phone?: "",
                onConfirm = { name, email, phone ->
                    viewModel.updateWorker(worker.id, name, email, phone) {
                        workerToEdit = null
                    }
                },
                onDismiss = { workerToEdit = null }
            )
        }

        // Eliminar empleado
        workerToDelete?.let { worker ->
            DeleteWorkerDialog(
                workerName = worker.fullName ?: worker.username,
                onConfirm = {
                    viewModel.deleteWorker(worker.id) {
                        workerToDelete = null
                    }
                },
                onDismiss = { workerToDelete = null }
            )
        }
    }
}

