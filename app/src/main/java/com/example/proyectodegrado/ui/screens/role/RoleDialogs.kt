package com.example.proyectodegrado.ui.screens.role

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Permit
import com.example.proyectodegrado.data.model.Role

@Composable
fun CreateEditRoleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, isAdmin: Boolean) -> Unit,
    role: Role?,
    allPermits: List<Permit>,
    selectedPermitId: Int?,
    onPermitSelected: (permitId: Int) -> Unit
) {
    if (!show) return

    var name by remember { mutableStateOf(role?.name ?: "") }
    var description by remember { mutableStateOf(role?.description ?: "") }
    var isAdmin by remember { mutableStateOf(role?.isAdmin ?: false) }

    // Re-Sync state
    LaunchedEffect(role) {
        name = role?.name ?: ""
        description = role?.description ?: ""
        isAdmin = role?.isAdmin ?: false
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.large) {
            Column(modifier = Modifier.width(IntrinsicSize.Max).padding(24.dp)) {
                Text(
                    text = if (role == null) "Crear Nuevo Rol" else "Editar Rol",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Rol") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                    Text("Es Administrador")
                }
                Spacer(Modifier.height(16.dp))

                Text("Permisos", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(modifier = Modifier.heightIn(max = 200.dp)) {
                    allPermits.forEach { permit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPermitSelected(permit.id) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (permit.id == selectedPermitId),
                                onClick = { onPermitSelected(permit.id) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = permit.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onSave(name, description, isAdmin) }, enabled = name.isNotBlank()) {
                        Text("Guardar")
                    }
                }
            }
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