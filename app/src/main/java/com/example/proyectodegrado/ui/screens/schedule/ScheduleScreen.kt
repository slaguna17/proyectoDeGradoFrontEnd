package com.example.proyectodegrado.ui.screens.schedule

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
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.ui.components.RefreshableContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var scheduleToEdit by remember { mutableStateOf<Schedule?>(null) }
    var scheduleToDelete by remember { mutableStateOf<Schedule?>(null) }

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
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
                schedules.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay turnos creados.")
                    }
                }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
                        items(schedules, key = { it.id }) { schedule ->
                            ScheduleItem(
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
                onSuccess = {
                    showSnackbar("Turno creado")
                    refreshData()
                },
                onError = { error -> showSnackbar(error) }
            )
            showCreateDialog = false
        }
    )

    EditScheduleDialog(
        show = showEditDialog,
        schedule = scheduleToEdit,
        onDismiss = { showEditDialog = false },
        onConfirm = { request ->
            scheduleToEdit?.let {
                viewModel.updateSchedule(it.id, request,
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

    DeleteScheduleDialog(
        show = showDeleteDialog,
        schedule = scheduleToDelete,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            scheduleToDelete?.let {
                viewModel.deleteSchedule(it.id,
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
