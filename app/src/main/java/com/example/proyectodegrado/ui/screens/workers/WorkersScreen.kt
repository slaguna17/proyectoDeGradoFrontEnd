package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyectodegrado.data.model.EmployeeUI
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme // Asegúrate que el nombre del tema sea correcto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersScreen(
    workersViewModel: WorkersViewModel,
    // Pasaremos el storeViewModel y scheduleViewModel al diálogo cuando lo creemos
    // storeViewModel: StoreViewModel, // Descomentar si se usa para diálogos
    // scheduleViewModel: ScheduleViewModel, // Descomentar si se usa para diálogos
    onNavigateToCreateWorker: () -> Unit, // Para navegar o mostrar diálogo de creación
    onNavigateToEditWorker: (Int) -> Unit // Para navegar o mostrar diálogo de edición (pasando userId)
) {
    val employeesList by workersViewModel.employees.collectAsStateWithLifecycle()
    val isLoading by workersViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by workersViewModel.errorMessage.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var showSearchBar by rememberSaveable { mutableStateOf(false) }

    // Estados para los diálogos
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var employeeToEdit by remember { mutableStateOf<EmployeeUI?>(null) }
    var employeeToDelete by remember { mutableStateOf<EmployeeUI?>(null) }

    ProyectoDeGradoTheme { // Aplicando tu tema
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (showSearchBar) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar empleado...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        } else {
                            Text("Empleados")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        // Aquí podrías añadir más acciones si es necesario
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    workersViewModel.resetEmployeeFormState() // Prepara el form state para creación
                    showCreateDialog = true
                    // onNavigateToCreateWorker() // Alternativa si navegas a otra pantalla
                }) {
                    Icon(Icons.Filled.Add, "Añadir Empleado")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                } else if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    val filteredEmployees = if (searchQuery.text.isBlank()) {
                        employeesList
                    } else {
                        employeesList.filter { employee ->
                            employee.username.contains(searchQuery.text, ignoreCase = true) ||
                                    (employee.fullName?.contains(searchQuery.text, ignoreCase = true) == true)
                        }
                    }

                    if (filteredEmployees.isEmpty() && searchQuery.text.isNotBlank()) {
                        Text(
                            "No se encontraron empleados con ese nombre.",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    } else if (employeesList.isEmpty()){
                        Text(
                            "No hay empleados registrados.",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredEmployees, key = { it.userId }) { employee ->
                                WorkerItem( // Este es el Composable que definimos en WorkerItems.kt
                                    employee = employee,
                                    onEditClick = {
                                        workersViewModel.resetEmployeeFormState(employeeToEdit = employee)
                                        employeeToEdit = employee
                                        showEditDialog = true
                                        // onNavigateToEditWorker(employee.userId) // Alternativa si navegas
                                    },
                                    onDeleteClick = {
                                        employeeToDelete = employee
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Diálogos ---
    // La lógica para mostrar/ocultar los diálogos y pasarles los datos
    if (showCreateDialog) {
        CreateEmployeeDialog( // Definido en WorkerDialogs.kt
            showDialog = showCreateDialog,
            formState = workersViewModel.employeeFormState.collectAsStateWithLifecycle().value,
            imageUploadState = workersViewModel.imageUploadUiState.collectAsStateWithLifecycle().value,
            availableUsers = workersViewModel.availableUsers.collectAsStateWithLifecycle().value,
            availableStores = workersViewModel.availableStores.collectAsStateWithLifecycle().value,
            availableShifts = workersViewModel.availableShifts.collectAsStateWithLifecycle().value,
            onFormStateChange = workersViewModel::updateEmployeeFormState,
            onImageUriSelected = workersViewModel::handleEmployeeAvatarSelection,
            isNewUserMode = workersViewModel.isNewUserMode.collectAsStateWithLifecycle().value,
            onToggleNewUserMode = workersViewModel::toggleNewUserMode,
            onDismiss = { showCreateDialog = false },
            onCreateClick = {
                workersViewModel.createOrUpdateEmployee()
                // Considera cerrar el diálogo basado en el resultado del ViewModel (éxito/error)
                // Por ahora, se cierra si no hay error y no está cargando (estado ideal post-operación)
                if (workersViewModel.errorMessage.value == null && !workersViewModel.isLoading.value) {
                    showCreateDialog = false
                }
            }
        )
    }

    employeeToEdit?.let { emp ->
        if (showEditDialog) {
            EditEmployeeDialog( // Definido en WorkerDialogs.kt
                showDialog = showEditDialog,
                employee = emp,
                formState = workersViewModel.employeeFormState.collectAsStateWithLifecycle().value,
                imageUploadState = workersViewModel.imageUploadUiState.collectAsStateWithLifecycle().value,
                availableStores = workersViewModel.availableStores.collectAsStateWithLifecycle().value,
                availableShifts = workersViewModel.availableShifts.collectAsStateWithLifecycle().value,
                onFormStateChange = workersViewModel::updateEmployeeFormState,
                onImageUriSelected = workersViewModel::handleEmployeeAvatarSelection,
                onDismiss = {
                    showEditDialog = false
                    employeeToEdit = null
                    workersViewModel.resetEmployeeFormState()
                },
                onSaveClick = {
                    workersViewModel.createOrUpdateEmployee()
                    if (workersViewModel.errorMessage.value == null && !workersViewModel.isLoading.value) {
                        showEditDialog = false
                        employeeToEdit = null
                        workersViewModel.resetEmployeeFormState()
                    }
                }
            )
        }
    }

    employeeToDelete?.let { emp ->
        if (showDeleteDialog) {
            DeleteEmployeeConfirmationDialog( // Definido en WorkerDialogs.kt
                employeeName = emp.fullName ?: emp.username,
                onConfirmDelete = {
                    workersViewModel.deleteEmployee(emp.userId)
                    showDeleteDialog = false
                    employeeToDelete = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    employeeToDelete = null
                }
            )
        }
    }
}