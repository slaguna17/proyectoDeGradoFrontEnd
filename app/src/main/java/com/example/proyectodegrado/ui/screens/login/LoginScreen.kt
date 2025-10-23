package com.example.proyectodegrado.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.di.AppPreferences
import kotlin.math.log

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    val context = LocalContext.current
    val savedEmail = remember { AppPreferences(context).getUserEmail() ?: "" }

    var email by remember { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(savedEmail.isNotBlank()) }

    val loginState by viewModel.loginState.observeAsState(initial = LoginState.Idle)
    val isLoading = loginState is LoginState.Loading
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val logo = painterResource(R.drawable.logonobackground)

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is LoginState.Error -> {
                errorMsg = state.message
            }
            else -> {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 70.dp),
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
            enabled = !isLoading,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Contraseña") },
            enabled = !isLoading,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Mostramos el mensaje de error aquí si existe
        errorMsg?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                errorMsg = null
                viewModel.login(email, password, rememberMe)
            },
            enabled = loginState !is LoginState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loginState is LoginState.Loading) "Ingresando..." else "Ingresar")
        }
        when (loginState) {
            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is LoginState.Error -> {
                val msg = (loginState as LoginState.Error).message
                LaunchedEffect(msg) {
                    // TODO: show snackbar/dialog con msg
                    viewModel.clearError()
                }
            }
            else -> Unit
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                enabled = !isLoading
            )
            Text(text = "Recuérdame")
        }
    }
}