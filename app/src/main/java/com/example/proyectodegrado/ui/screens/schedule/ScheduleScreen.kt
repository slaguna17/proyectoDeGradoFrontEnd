package com.example.proyectodegrado.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.ui.components.RefreshableContainer

@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: ScheduleViewModel
) {
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Estados para controlar la visibilidad de los diálogos
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados para guardar el turno a editar o eliminar
    var scheduleToEdit by remember { mutableStateOf<Schedule?>(null) }
    var scheduleToDelete by remember { mutableStateOf<Schedule?>(null) }

    fun showSnackbar(message: String) {
        // Implementa tu lógica de snackbar si lo deseas
    }

    fun refreshData() {
        isRefreshing = true
        viewModel.fetchSchedules(
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showCreateDialog = true }, // Abre el diálogo de creación
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("+ Crear Turno")
            }
            Spacer(Modifier.height(8.dp))

            RefreshableContainer(
                refreshing = isRefreshing,
                onRefresh = ::refreshData
            ) {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (schedules.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay turnos creados.")
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(schedules, key = { it.id }) { schedule ->
                            ScheduleItem (
                                schedule = schedule,
                                onEdit = {
                                    scheduleToEdit = it
                                    showEditDialog = true
                                },
                                onDelete = {
                                    scheduleToDelete = it
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    CreateScheduleDialog(
        show = showCreateDialog,
        onDismiss = { showCreateDialog = false },
        onConfirm = { request ->
            viewModel.createSchedule(request,
                onSuccess = { refreshData(); showSnackbar("Turno creado") },
                onError = { error -> showSnackbar(error) }
            )
            showCreateDialog = false
        }
    )

    EditScheduleDialog (
        show = showEditDialog,
        schedule = scheduleToEdit,
        onDismiss = { showEditDialog = false },
        onConfirm = { request ->
            scheduleToEdit?.let {
                viewModel.updateSchedule(it.id, request,
                    onSuccess = { refreshData(); showSnackbar("Turno actualizado") },
                    onError = { error -> showSnackbar(error) }
                )
            }
            showEditDialog = false
        }
    )

    DeleteScheduleDialog(
        show = showDeleteDialog,
        schedule = scheduleToDelete,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            scheduleToDelete?.let {
                viewModel.deleteSchedule(it.id,
                    onSuccess = { refreshData(); showSnackbar("Turno eliminado") },
                    onError = { error -> showSnackbar(error) }
                )
            }
            showDeleteDialog = false
        }
    )
}