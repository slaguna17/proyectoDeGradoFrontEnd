package com.example.proyectodegrado.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.R

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    var fullName by remember { mutableStateOf(user?.fullName.orEmpty()) }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }
    var phone by remember { mutableStateOf(user?.phone.orEmpty()) }
    var isEditing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.lemon_drink),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nombre completo") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        if (isEditing) {
            Row {
                Button(onClick = {
                    viewModel.updateProfile(fullName, email, phone,
                        onSuccess = {
                            isEditing = false
                            message = "Datos actualizados correctamente"
                        },
                        onError = { msg ->
                            message = msg
                        }
                    )
                }) {
                    Text("Guardar")
                }
                Spacer(Modifier.width(16.dp))
                TextButton(onClick = {
                    // Cancela edición, recarga datos
                    fullName = user?.fullName.orEmpty()
                    email = user?.email.orEmpty()
                    phone = user?.phone.orEmpty()
                    isEditing = false
                }) {
                    Text("Cancelar")
                }
            }
        } else {
            Button(onClick = { isEditing = true }) {
                Text("Editar")
            }
        }

        if (message.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
