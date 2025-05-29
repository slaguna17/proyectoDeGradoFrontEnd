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
import com.example.proyectodegrado.data.model.Worker

@Composable
fun WorkersScreen(viewModel: WorkersViewModel, navController: NavController) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val employees by viewModel.employees.collectAsState()
    val dialogContext by viewModel.selectedWorkerContext.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onSearchQueryChange("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Buscar empleado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (employees.isEmpty()) {
                Text("No hay empleados", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(employees) { worker ->
                        WorkerItem(
                            worker = worker,
                            onAssignClick = { viewModel.openAssignScheduleDialog(worker) },
                            onEditClick = { /* TODO */ },
                            onDeleteClick = { /* TODO */ }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("registerEmployee") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Agregar empleado")
        }
    }

    if (dialogContext != null) {
        AssignScheduleDialog(
            workerContext = dialogContext!!,
            onDismiss = { viewModel.closeDialog() },
            onFormStateChange = viewModel::updateAssignScheduleForm,
            onConfirm = viewModel::confirmScheduleAssignment
        )
    }
}
