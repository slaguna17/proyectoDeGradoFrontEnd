package com.example.proyectodegrado.ui.screens.role

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RoleRequest
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest

@Composable
private fun RoleDialogContent(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isAdmin: Boolean,
    onAdminChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
    submitLabel: String
) {
    var showError by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(16.dp)
            .widthIn(min = 320.dp, max = 400.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                onNameChange(it)
                showError = false
            },
            label = { Text("Nombre del Rol") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && name.isBlank()
        )
        if (showError && name.isBlank()) {
            Text(
                text = "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Checkbox(
            checked = false,
            onCheckedChange = onAdminChange,
            modifier = Modifier.padding(1.dp),
            enabled = true
        )
        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                if (name.isBlank()) {
                    showError = true
                } else {
                    onSubmit()
                }
            }) { Text(submitLabel) }
        }
    }
}

@Composable
fun CreateRoleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (RoleRequest) -> Unit
) {
    if (!show) return
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            RoleDialogContent(
                title = "Crear Turno",
                name = name, onNameChange = { name = it },
                description = description, onDescriptionChange = { description = it },
                isAdmin = isAdmin, onAdminChange = { isAdmin = it },
                onDismiss = onDismiss,
                onSubmit = {
                    onConfirm(RoleRequest(name, description, isAdmin))
                },
                submitLabel = "Crear"
            )
        }
    }
}

@Composable
fun EditRoleDialog(
    show: Boolean,
    role: Role?,
    onDismiss: () -> Unit,
    onConfirm: (RoleRequest) -> Unit
) {
    if (!show || role == null) return

    var name by remember { mutableStateOf(role.name) }
    var description by remember { mutableStateOf(role.description) }
    var isAdmin by remember { mutableStateOf(role.isAdmin) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            RoleDialogContent(
                title = "Editar Turno",
                name = name, onNameChange = { name = it },
                description = description, onDescriptionChange = { description = it },
                isAdmin = isAdmin, onAdminChange = { isAdmin = it },
                onDismiss = onDismiss,
                onSubmit = {
                    onConfirm(RoleRequest(name, description, isAdmin))
                },
                submitLabel = "Guardar"
            )
        }
    }
}


@Composable
fun DeleteRoleDialog(
    show: Boolean,
    role: Role?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show || role == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar el rol \"${role.name}\"?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Eliminar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}