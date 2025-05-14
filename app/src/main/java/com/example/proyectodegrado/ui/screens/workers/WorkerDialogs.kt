package com.example.proyectodegrado.ui.screens.workers

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.CreateEmployeeFormState
import com.example.proyectodegrado.data.model.EmployeeUI
import com.example.proyectodegrado.data.model.Schedule // Importando tu modelo Schedule
import com.example.proyectodegrado.data.model.ShiftInfo // Usaremos ShiftInfo que tiene id y name
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEmployeeDialog(
    showDialog: Boolean,
    formState: CreateEmployeeFormState,
    imageUploadState: UploadImageState,
    availableUsers: List<User>,
    availableStores: List<Store>,
    availableShifts: List<ShiftInfo>, // Esta es List<ShiftInfo> que viene del ViewModel
    onFormStateChange: (CreateEmployeeFormState) -> Unit,
    onImageUriSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onCreateClick: () -> Unit,
    isNewUserMode: Boolean,
    onToggleNewUserMode: (Boolean) -> Unit
) {
    if (!showDialog) return

    var userDropdownExpanded by remember { mutableStateOf(false) }
    var storeDropdownExpanded by remember { mutableStateOf(false) }
    var principalShiftDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(vertical = 16.dp) // Padding vertical para el diálogo
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(if (isNewUserMode) "Crear Nuevo Empleado" else "Asignar Empleado Existente", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isNewUserMode,
                        onCheckedChange = onToggleNewUserMode
                    )
                    Text("Crear nuevo usuario", modifier = Modifier.clickable { onToggleNewUserMode(!isNewUserMode) })
                }
                Spacer(Modifier.height(8.dp))

                if (isNewUserMode) {
                    OutlinedTextField(
                        value = formState.username,
                        onValueChange = { onFormStateChange(formState.copy(username = it)) },
                        label = { Text("Nombre de Usuario*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.fullName,
                        onValueChange = { onFormStateChange(formState.copy(fullName = it)) },
                        label = { Text("Nombre Completo*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { onFormStateChange(formState.copy(email = it)) },
                        label = { Text("Email*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.password,
                        onValueChange = { onFormStateChange(formState.copy(password = it)) },
                        label = { Text("Contraseña*") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = userDropdownExpanded,
                        onExpandedChange = { userDropdownExpanded = !userDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = availableUsers.find { it.id == formState.selectedUserId }?.username ?: "Seleccionar Usuario*",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Usuario Existente*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = userDropdownExpanded,
                            onDismissRequest = { userDropdownExpanded = false }
                        ) {
                            availableUsers.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text("${user.full_name ?: user.username} (${user.username})") },
                                    onClick = {
                                        onFormStateChange(formState.copy(selectedUserId = user.id, username = user.username, fullName = user.full_name ?: "", email = user.email ?: "", avatarUrl = user.avatar ))
                                        userDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = storeDropdownExpanded,
                    onExpandedChange = { storeDropdownExpanded = !storeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = availableStores.find { it.id == formState.selectedStoreId }?.name ?: "Seleccionar Tienda*",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tienda Asignada*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = storeDropdownExpanded,
                        onDismissRequest = { storeDropdownExpanded = false }
                    ) {
                        availableStores.forEach { store ->
                            DropdownMenuItem(
                                text = { Text(store.name) },
                                onClick = {
                                    onFormStateChange(formState.copy(selectedStoreId = store.id))
                                    storeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = principalShiftDropdownExpanded,
                    onExpandedChange = { principalShiftDropdownExpanded = !principalShiftDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = availableShifts.find { it.id == formState.selectedPrincipalShiftId }?.name ?: "Seleccionar Turno Principal*",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Turno Principal*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = principalShiftDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = principalShiftDropdownExpanded,
                        onDismissRequest = { principalShiftDropdownExpanded = false }
                    ) {
                        availableShifts.forEach { shift ->
                            DropdownMenuItem(
                                text = { Text(shift.name ?: "Turno ID: ${shift.id}") },
                                onClick = {
                                    onFormStateChange(formState.copy(selectedPrincipalShiftId = shift.id))
                                    principalShiftDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Text("Turnos Adicionales (opcional):", style = MaterialTheme.typography.labelMedium)
                Column { // Envuelve los checkboxes en una columna para mejor diseño si son muchos
                    availableShifts.forEach { shift ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val currentSelection = formState.selectedShiftIds.toMutableList()
                                    if (currentSelection.contains(shift.id)) {
                                        currentSelection.remove(shift.id)
                                    } else {
                                        currentSelection.add(shift.id)
                                    }
                                    onFormStateChange(formState.copy(selectedShiftIds = currentSelection))
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = formState.selectedShiftIds.contains(shift.id),
                                onCheckedChange = { checked ->
                                    val currentSelection = formState.selectedShiftIds.toMutableList()
                                    if (checked) {
                                        if (!currentSelection.contains(shift.id)) currentSelection.add(shift.id)
                                    } else {
                                        currentSelection.remove(shift.id)
                                    }
                                    onFormStateChange(formState.copy(selectedShiftIds = currentSelection))
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(shift.name ?: "Turno ID: ${shift.id}")
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                UploadImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    currentImageUrl = formState.avatarUrl,
                    uploadState = imageUploadState,
                    onImageSelected = onImageUriSelected
                )
                if (imageUploadState is UploadImageState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Error imagen: ${imageUploadState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onCreateClick,
                        enabled = imageUploadState !is UploadImageState.Loading && formState.isLoading.not()
                    ) {
                        Text(if (formState.selectedUserId != null && !isNewUserMode) "Guardar" else "Crear")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployeeDialog(
    showDialog: Boolean,
    employee: EmployeeUI, // Empleado actual para pre-cargar datos
    formState: CreateEmployeeFormState, // Usamos el mismo formState para consistencia
    imageUploadState: UploadImageState,
    availableStores: List<Store>,
    availableShifts: List<ShiftInfo>,
    onFormStateChange: (CreateEmployeeFormState) -> Unit,
    onImageUriSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onSaveClick: () -> Unit
) {
    if (!showDialog) return

    var storeDropdownExpanded by remember { mutableStateOf(false) }
    var principalShiftDropdownExpanded by remember { mutableStateOf(false) }

    // El ViewModel se encarga de poblar el formState con employeeToEdit en resetEmployeeFormState

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Editar Asignación de Empleado", style = MaterialTheme.typography.titleLarge)
                Text(employee.fullName ?: employee.username, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                // Dropdown para Tienda
                ExposedDropdownMenuBox(
                    expanded = storeDropdownExpanded,
                    onExpandedChange = { storeDropdownExpanded = !storeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = availableStores.find { it.id == formState.selectedStoreId }?.name ?: "Seleccionar Tienda*",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tienda Asignada*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = storeDropdownExpanded,
                        onDismissRequest = { storeDropdownExpanded = false }
                    ) {
                        availableStores.forEach { store ->
                            DropdownMenuItem(
                                text = { Text(store.name) },
                                onClick = {
                                    onFormStateChange(formState.copy(selectedStoreId = store.id))
                                    storeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Dropdown para Turno Principal
                ExposedDropdownMenuBox(
                    expanded = principalShiftDropdownExpanded,
                    onExpandedChange = { principalShiftDropdownExpanded = !principalShiftDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = availableShifts.find { it.id == formState.selectedPrincipalShiftId }?.name ?: "Seleccionar Turno Principal*",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Turno Principal*") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = principalShiftDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = principalShiftDropdownExpanded,
                        onDismissRequest = { principalShiftDropdownExpanded = false }
                    ) {
                        availableShifts.forEach { shift ->
                            DropdownMenuItem(
                                text = { Text(shift.name ?: "Turno ID: ${shift.id}") },
                                onClick = {
                                    onFormStateChange(formState.copy(selectedPrincipalShiftId = shift.id))
                                    principalShiftDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Text("Turnos Adicionales (en la tienda seleccionada):", style = MaterialTheme.typography.labelMedium)
                Column {
                    availableShifts.forEach { shift ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val currentSelection = formState.selectedShiftIds.toMutableList()
                                    if (currentSelection.contains(shift.id)) {
                                        currentSelection.remove(shift.id)
                                    } else {
                                        currentSelection.add(shift.id)
                                    }
                                    onFormStateChange(formState.copy(selectedShiftIds = currentSelection))
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = formState.selectedShiftIds.contains(shift.id),
                                onCheckedChange = { checked ->
                                    val currentSelection = formState.selectedShiftIds.toMutableList()
                                    if (checked) {
                                        if (!currentSelection.contains(shift.id)) currentSelection.add(shift.id)
                                    } else {
                                        currentSelection.remove(shift.id)
                                    }
                                    onFormStateChange(formState.copy(selectedShiftIds = currentSelection))
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(shift.name ?: "Turno ID: ${shift.id}")
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                UploadImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    currentImageUrl = formState.avatarUrl,
                    uploadState = imageUploadState,
                    onImageSelected = onImageUriSelected
                )
                if (imageUploadState is UploadImageState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Error imagen: ${imageUploadState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onSaveClick,
                        enabled = imageUploadState !is UploadImageState.Loading && formState.isLoading.not()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteEmployeeConfirmationDialog(
    employeeName: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar la asignación de empleado para '$employeeName'?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}