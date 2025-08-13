package com.example.proyectodegrado.ui.screens.cash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OpenCashDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val valid = amountText.toDoubleOrNull()?.let { it >= 0.0 } == true

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(amountText.toDouble()) }, enabled = valid) {
                Text("Abrir caja")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Abrir caja") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Monto de apertura")
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    placeholder = { Text("0.00") }
                )
            }
        }
    )
}

@Composable
fun MovementDialog(
    onDismiss: () -> Unit,
    onConfirm: (direction: String, amount: Double, category: String?, notes: String?) -> Unit
) {
    var direction by remember { mutableStateOf("IN") } // IN | OUT | ADJUST
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val valid = amountText.toDoubleOrNull()?.let { it > 0.0 } == true

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(direction, amountText.toDouble(), category.ifBlank { null }, notes.ifBlank { null })
            }, enabled = valid) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Movimiento de caja") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo:")
                    AssistChip(
                        onClick = { direction = "IN" },
                        label = { Text("Ingreso") },
                        leadingIcon = null,
                        enabled = direction != "IN"
                    )
                    AssistChip(
                        onClick = { direction = "OUT" },
                        label = { Text("Egreso") },
                        leadingIcon = null,
                        enabled = direction != "OUT"
                    )
                    AssistChip(
                        onClick = { direction = "ADJUST" },
                        label = { Text("Ajuste") },
                        leadingIcon = null,
                        enabled = direction != "ADJUST"
                    )
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (opcional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    singleLine = false,
                    minLines = 2
                )
            }
        }
    )
}

@Composable
fun CloseCashDialog(
    expected: Double,
    onDismiss: () -> Unit,
    onConfirm: (closingAmount: Double?) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val showDifference = amount != null

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(amount) }) { Text("Cerrar caja") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Cerrar caja") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Cierre esperado: %.2f".format(expected))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto contado (opcional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    placeholder = { Text("Dejar vacío para usar el esperado") }
                )
                if (showDifference) {
                    val diff = amount!! - expected
                    Text(if (diff >= 0) "Diferencia: +%.2f".format(diff) else "Diferencia: %.2f".format(diff))
                }
            }
        }
    )
}
