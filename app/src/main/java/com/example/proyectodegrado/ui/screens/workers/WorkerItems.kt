package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.data.model.Worker

@Composable
fun WorkerItem(
    worker: Worker,
    onAssignClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text("#${worker.id} - ${worker.username}", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAssignClick) {
                Text("Asignar Horario")
            }
            OutlinedButton(onClick = onEditClick) {
                Text("Editar")
            }
            OutlinedButton(onClick = onDeleteClick) {
                Text("Borrar")
            }
        }
    }
}
