package com.example.proyectodegrado.ui.screens.store

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
private fun StoreDialogContent(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    logo: String,
    onLogoChange: (String) -> Unit,
    history: String,
    onHistoryChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
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
                showError = false
                onNameChange(it)
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && name.isBlank()
        )
        if (showError && name.isBlank()) {
            Text(
                text = "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = logo,
            onValueChange = onLogoChange,
            label = { Text("URL Logo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = history,
            onValueChange = onHistoryChange,
            label = { Text("Historia") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
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

// --- Diálogo para CREAR ---
@Composable
fun CreateStoreDialog(
    show: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    logo: String,
    onLogoChange: (String) -> Unit,
    history: String,
    onHistoryChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            StoreDialogContent(
                title = "Crear Tienda",
                name = name,
                onNameChange = onNameChange,
                address = address,
                onAddressChange = onAddressChange,
                city = city,
                onCityChange = onCityChange,
                logo = logo,
                onLogoChange = onLogoChange,
                history = history,
                onHistoryChange = onHistoryChange,
                phone = phone,
                onPhoneChange = onPhoneChange,
                onDismiss = onDismiss,
                onSubmit = onSubmit,
                submitLabel = "Crear"
            )
        }
    }
}

// --- Diálogo para EDITAR ---
@Composable
fun EditStoreDialog(
    show: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    logo: String,
    onLogoChange: (String) -> Unit,
    history: String,
    onHistoryChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            StoreDialogContent(
                title = "Editar Tienda",
                name = name,
                onNameChange = onNameChange,
                address = address,
                onAddressChange = onAddressChange,
                city = city,
                onCityChange = onCityChange,
                logo = logo,
                onLogoChange = onLogoChange,
                history = history,
                onHistoryChange = onHistoryChange,
                phone = phone,
                onPhoneChange = onPhoneChange,
                onDismiss = onDismiss,
                onSubmit = onSubmit,
                submitLabel = "Guardar"
            )
        }
    }
}

// --- Diálogo para ELIMINAR ---
@Composable
fun DeleteStoreDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(20.dp)) {
                Text(message, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
