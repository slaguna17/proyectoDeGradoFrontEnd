package com.example.proyectodegrado.ui.screens.store

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState

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
    uploadState: UploadImageState,
    onPickLogo: (Uri?) -> Unit,
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

        // Selector de imagen (S3)
        UploadImage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            currentImageUrl = if (logo.isBlank()) null else logo,
            uploadState = uploadState,
            onImageSelected = onPickLogo
        )
        if (uploadState is UploadImageState.Error) {
            Spacer(Modifier.height(8.dp))
            Text("Error de imagen: ${uploadState.message}", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))
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
        OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = city, onValueChange = onCityChange, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        // Campo opcional para URL externa (si no usas S3)
        OutlinedTextField(
            value = logo,
            onValueChange = onLogoChange,
            label = { Text("Logo (URL opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = history, onValueChange = onHistoryChange, label = { Text("Historia") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Spacer(Modifier.width(8.dp))
            Button(
                enabled = uploadState is UploadImageState.Idle,
                onClick = {
                    if (name.isBlank()) { showError = true } else onSubmit()
                }
            ) { Text(submitLabel) }
        }
    }
}

// --- CREAR ---
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
    uploadState: UploadImageState,
    onPickLogo: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            StoreDialogContent(
                title = "Crear Tienda",
                name = name, onNameChange = onNameChange,
                address = address, onAddressChange = onAddressChange,
                city = city, onCityChange = onCityChange,
                logo = logo, onLogoChange = onLogoChange,
                history = history, onHistoryChange = onHistoryChange,
                phone = phone, onPhoneChange = onPhoneChange,
                uploadState = uploadState, onPickLogo = onPickLogo,
                onDismiss = onDismiss, onSubmit = onSubmit, submitLabel = "Crear"
            )
        }
    }
}

// --- EDITAR ---
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
    uploadState: UploadImageState,
    onPickLogo: (Uri?) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            StoreDialogContent(
                title = "Editar Tienda",
                name = name, onNameChange = onNameChange,
                address = address, onAddressChange = onAddressChange,
                city = city, onCityChange = onCityChange,
                logo = logo, onLogoChange = onLogoChange,
                history = history, onHistoryChange = onHistoryChange,
                phone = phone, onPhoneChange = onPhoneChange,
                uploadState = uploadState, onPickLogo = onPickLogo,
                onDismiss = onDismiss, onSubmit = onSubmit, submitLabel = "Guardar"
            )
        }
    }
}

// --- ELIMINAR ---
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
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(min = 320.dp, max = 400.dp)
            ) {
                Text("Eliminar Tienda", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) { Text("Eliminar") }
                }
            }
        }
    }
}

