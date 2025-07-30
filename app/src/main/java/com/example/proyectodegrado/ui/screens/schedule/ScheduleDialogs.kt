package com.example.proyectodegrado.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest

@Composable
private fun ScheduleDialogContent(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    length: String,
    onLengthChange: (String) -> Unit,
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
            label = { Text("Nombre del Turno") },
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
            value = startTime,
            onValueChange = onStartTimeChange,
            label = { Text("Hora de Inicio (ej: 08:00)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = endTime,
            onValueChange = onEndTimeChange,
            label = { Text("Hora de Fin (ej: 16:00)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = length,
            onValueChange = onLengthChange,
            label = { Text("Duración (horas)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(16.dp))

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
fun CreateScheduleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleRequest) -> Unit
) {
    if (!show) return

    var name by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            ScheduleDialogContent(
                title = "Crear Turno",
                name = name, onNameChange = { name = it },
                startTime = startTime, onStartTimeChange = { startTime = it },
                endTime = endTime, onEndTimeChange = { endTime = it },
                length = length, onLengthChange = { length = it },
                onDismiss = onDismiss,
                onSubmit = {
                    onConfirm(ScheduleRequest(name, length.toIntOrNull() ?: 0, startTime, endTime))
                },
                submitLabel = "Crear"
            )
        }
    }
}

@Composable
fun EditScheduleDialog(
    show: Boolean,
    schedule: Schedule?,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleRequest) -> Unit
) {
    if (!show || schedule == null) return

    var name by remember { mutableStateOf(schedule.name) }
    var startTime by remember { mutableStateOf(schedule.startTime) }
    var endTime by remember { mutableStateOf(schedule.endTime) }
    var length by remember { mutableStateOf(schedule.length.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            ScheduleDialogContent(
                title = "Editar Turno",
                name = name, onNameChange = { name = it },
                startTime = startTime, onStartTimeChange = { startTime = it },
                endTime = endTime, onEndTimeChange = { endTime = it },
                length = length, onLengthChange = { length = it },
                onDismiss = onDismiss,
                onSubmit = {
                    onConfirm(ScheduleRequest(name, length.toIntOrNull() ?: 0, startTime, endTime))
                },
                submitLabel = "Guardar"
            )
        }
    }
}


@Composable
fun DeleteScheduleDialog(
    show: Boolean,
    schedule: Schedule?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show || schedule == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que deseas eliminar el turno \"${schedule.name}\"?") },
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