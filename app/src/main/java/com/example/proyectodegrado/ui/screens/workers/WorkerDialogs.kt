package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.SelectedWorkerContext
import com.example.proyectodegrado.data.model.AssignScheduleFormState

@Composable
fun AssignScheduleDialog(
    workerContext: SelectedWorkerContext,
    onDismiss: () -> Unit,
    onFormStateChange: (AssignScheduleFormState) -> Unit,
    onConfirm: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(16.dp)) {
                Text("Asignar Horario a ${workerContext.worker.username}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = workerContext.formState.storeId?.toString() ?: "",
                    onValueChange = {
                        onFormStateChange(workerContext.formState.copy(storeId = it.toIntOrNull()))
                    },
                    label = { Text("ID Tienda") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = workerContext.formState.scheduleId?.toString() ?: "",
                    onValueChange = {
                        onFormStateChange(workerContext.formState.copy(scheduleId = it.toIntOrNull()))
                    },
                    label = { Text("ID Turno") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onConfirm(
                            onDismiss,
                            { /* mensaje de error */ }
                        )
                    }) {
                        Text("Asignar")
                    }
                }
            }
        }
    }
}
