package com.example.proyectodegrado.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    isLoading: Boolean = false,
    message: String? = null,
) {
    if (!show) return

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    // Validación simple
                    if (email.isBlank() || !email.contains("@")) {
                        emailError = "Correo inválido"
                    } else {
                        emailError = null
                        onSend(email)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Recuperar contraseña") },
        text = {
            Column {
                Text("Ingresa tu correo y te enviaremos instrucciones para recuperar tu contraseña.")
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Correo electrónico") },
                    isError = emailError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(emailError ?: "", color = MaterialTheme.colorScheme.error)
                }
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
}
