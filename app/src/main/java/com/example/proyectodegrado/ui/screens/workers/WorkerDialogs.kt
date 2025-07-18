package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.AssignScheduleFormState
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.ui.components.StoreDropdown

@Composable
fun AssignScheduleDialog(
    stores: List<Store>,
    schedules: List<Schedule>,
    formState: AssignScheduleFormState,
    onFormStateChange: (AssignScheduleFormState) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String?
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(20.dp)) {
                Text("Asignar tienda y turno", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                StoreDropdown(
                    stores = stores,
                    selectedStoreId = formState.storeId,
                    onStoreSelected = { onFormStateChange(formState.copy(storeId = it)) }
                )
                Spacer(Modifier.height(8.dp))
                ScheduleDropdown(
                    schedules = schedules,
                    selectedScheduleId = formState.scheduleId,
                    onScheduleSelected = { onFormStateChange(formState.copy(scheduleId = it)) }
                )
                Spacer(Modifier.height(16.dp))
                if (!errorMessage.isNullOrBlank()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onConfirm) { Text("Asignar") }
                }
            }
        }
    }
}
