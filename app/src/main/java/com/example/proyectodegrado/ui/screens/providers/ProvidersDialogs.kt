package com.example.proyectodegrado.ui.screens.providers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.proyectodegrado.data.model.Product

@Composable
private fun ProviderDialogContent(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    contactPerson: String,
    onContactPersonChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
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
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = contactPerson,
            onValueChange = onContactPersonChange,
            label = { Text("Persona de contacto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas") },
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

@Composable
fun CreateProviderDialog(
    show: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    contactPerson: String,
    onContactPersonChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            ProviderDialogContent(
                title = "Crear Proveedor",
                name = name,
                onNameChange = onNameChange,
                address = address,
                onAddressChange = onAddressChange,
                email = email,
                onEmailChange = onEmailChange,
                phone = phone,
                onPhoneChange = onPhoneChange,
                contactPerson = contactPerson,
                onContactPersonChange = onContactPersonChange,
                notes = notes,
                onNotesChange = onNotesChange,
                onDismiss = onDismiss,
                onSubmit = onSubmit,
                submitLabel = "Crear"
            )
        }
    }
}

@Composable
fun EditProviderDialog(
    show: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    contactPerson: String,
    onContactPersonChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            ProviderDialogContent(
                title = "Editar Proveedor",
                name = name,
                onNameChange = onNameChange,
                address = address,
                onAddressChange = onAddressChange,
                email = email,
                onEmailChange = onEmailChange,
                phone = phone,
                onPhoneChange = onPhoneChange,
                contactPerson = contactPerson,
                onContactPersonChange = onContactPersonChange,
                notes = notes,
                onNotesChange = onNotesChange,
                onDismiss = onDismiss,
                onSubmit = onSubmit,
                submitLabel = "Guardar"
            )
        }
    }
}

@Composable
fun DeleteProviderDialog(
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

@Composable
fun ManageProviderProductsDialog(
    show: Boolean,
    providerName: String,
    products: List<Product>,
    selectedProductIds: Set<Int>,
    isSaving: Boolean,
    onToggleProduct: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    if (!show) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(360.dp)
            ) {
                Text(
                    text = "Productos de $providerName",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Selecciona los productos que puede proveer.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (products.isEmpty()) {
                        Text("No hay productos disponibles.")
                    } else {
                        products.sortedBy { it.name }.forEach { product ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "SKU: ${product.sku ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Checkbox(
                                    checked = selectedProductIds.contains(product.id),
                                    onCheckedChange = { onToggleProduct(product.id) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onSave,
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator()
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}