package com.example.proyectodegrado.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.ui.components.UploadImage
import com.example.proyectodegrado.ui.components.UploadImageState
import com.example.proyectodegrado.R

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val uploadState by viewModel.uploadState.collectAsState()
    var fullName by remember { mutableStateOf(user?.fullName.orEmpty()) }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }
    var phone by remember { mutableStateOf(user?.phone.orEmpty()) }
    var isEditing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // si cambia el user, refresca campos
    LaunchedEffect(user?.id) {
        fullName = user?.fullName.orEmpty()
        email = user?.email.orEmpty()
        phone = user?.phone.orEmpty()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        // Avatar (URL real del backend con fallback a la key/url previa o placeholder local)
        AsyncImage(
            model = user?.avatarUrl ?: user?.avatar ?: R.drawable.lemon_drink,
            contentDescription = "Avatar",
            modifier = Modifier.size(120.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(12.dp))

        if (isEditing) {
            // Picker/Upload a S3
            UploadImage(
                currentImageUrl = user?.avatarUrl ?: user?.avatar,
                uploadState = uploadState,
                onImageSelected = { uri: Uri? -> viewModel.handleAvatarSelection(uri) }
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    enabled = uploadState is UploadImageState.Idle,
                    onClick = {
                        viewModel.removeAvatar(
                            onSuccess = { message = "Avatar removido" },
                            onError = { msg -> message = msg }
                        )
                    }
                ) { Text("Quitar avatar") }
            }
            Spacer(Modifier.height(16.dp))
        }

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
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    enabled = uploadState is UploadImageState.Idle,
                    onClick = {
                        viewModel.updateProfile(
                            fullName, email, phone,
                            onSuccess = { isEditing = false; message = "Datos actualizados correctamente" },
                            onError = { msg -> message = msg }
                        )
                    }
                ) { Text("Guardar") }
                TextButton(onClick = {
                    fullName = user?.fullName.orEmpty()
                    email = user?.email.orEmpty()
                    phone = user?.phone.orEmpty()
                    isEditing = false
                }) { Text("Cancelar") }
            }
        } else {
            Button(onClick = { isEditing = true }) { Text("Editar") }
        }

        if (message.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
