package com.example.proyectodegrado.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    // --- Contexto / preferencias
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }

    // --- State UI
    var email by remember { mutableStateOf(prefs.getUserEmail() ?: "") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(email.isNotBlank()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val loginState by viewModel.loginState.observeAsState()
    val logo = painterResource(R.drawable.logonobackground)

    LaunchedEffect(loginState) {
        loginState?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                val resp = result.getOrNull()!!
                val user = resp.user
                val userId = user.id

                if (rememberMe) prefs.saveUserEmail(email) else prefs.clearUserEmail()

                prefs.saveUserId(userId.toString())
                prefs.saveUserName(user.username?.ifBlank { user.fullName })

                val storeId = prefs.getStoreId()?.toIntOrNull() ?: 1
                DependencyProvider.setCurrentSession(userId = userId, storeId = storeId)

                // 4) Navega a Home
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))

        Text(text = "¡Bienvenido!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Ingresa a tu cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isLoading
//            , modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Contraseña") },
            enabled = !isLoading
//            , modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMsg = null
                isLoading = true
                viewModel.login(email.trim(), password)
            },
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
//            , modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Ingresar")
            }
        }

//        errorMsg?.let {
//            Spacer(modifier = Modifier.height(12.dp))
//            Text(it, color = MaterialTheme.colorScheme.error)
//        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically
//            , modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                enabled = !isLoading
            )
            Text(text = "Recuérdame")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
