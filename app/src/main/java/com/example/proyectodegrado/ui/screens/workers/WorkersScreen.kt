package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    val selectedWorkerContext by viewModel.selectedWorkerContext.collectAsState()
    val assignError by viewModel.assignError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedStoreId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadStoresAndSchedules()
        viewModel.filterByStore(null)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("registerEmployee") },
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Agregar empleado")
            }

        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Text("No hay empleados en esta tienda", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(employees, key = { it.id }) { worker ->
                        WorkerItem(
                            worker = worker,
                            onAssignClick = { viewModel.openAssignScheduleDialog(worker) },
                            onEditClick = { /* editar empleado */ },
                            onDeleteClick = { /* eliminar empleado */ }
                        )
                    }
                }
            }
        }

        // Diálogo de asignar tienda/turno
        if (selectedWorkerContext != null) {
            AssignScheduleDialog(
                stores = stores,
                schedules = viewModel.schedules.collectAsState().value,
                formState = selectedWorkerContext!!.formState,
                onFormStateChange = viewModel::updateAssignForm,
                onConfirm = { viewModel.assignSchedule { /* éxito: podrías mostrar snackbar */ } },
                onDismiss = { viewModel.closeAssignScheduleDialog() },
                errorMessage = assignError
            )
        }
    }

}
