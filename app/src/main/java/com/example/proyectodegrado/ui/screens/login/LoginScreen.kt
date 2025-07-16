package com.example.proyectodegrado.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    // State Variables
    val loginState by viewModel.loginState.observeAsState()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appPrefs = remember { AppPreferences(context) }
    var rememberMe by remember { mutableStateOf(false) }

    // Images
    val logo = painterResource(R.drawable.logonobackground)

    // Screen variables
    var email by remember { mutableStateOf(appPrefs.getUserEmail() ?: "") }
    var password by remember { mutableStateOf("") }

    // Forgot password state
    val forgotResult by viewModel.forgotPasswordResult.observeAsState()
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotLoading by remember { mutableStateOf(false) }

    LaunchedEffect(forgotResult) {
        if (forgotLoading && forgotResult != null) {
            forgotLoading = false
            // Si éxito, cierra diálogo
            if (forgotResult?.isSuccess == true) {
                showForgotDialog = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))

        Text(text = "¡Bienvenido!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Ingresa a tu cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electronico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.login(email, password)
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Ingresar")
            }
        }

        loginState?.let { result ->
            when {
                result.isSuccess -> {
                    isLoading = false
                    Text("Login successful!")
                    val context = LocalContext.current
                    val userName = result.getOrNull()?.user?.username ?: "Usuario"
                    AppPreferences(context).saveUserName(userName)
                    if (rememberMe) {
                        appPrefs.saveUserEmail(email)
                    } else {
                        appPrefs.clearUserEmail()
                    }
                    navController.navigate("home")
                }
                result.isFailure -> {
                    isLoading = false
                    Text("Login failed: ${result.exceptionOrNull()?.message}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(text = "Recuérdame")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showForgotDialog = true }) {
            Text(text = "¿Olvidaste tu contraseña?")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "¿Eres nuevo? ")
            Text(
                text = "Registrate",
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }

        //ForgotPassword
        if (showForgotDialog) {
            ForgotPasswordDialog(
                show = showForgotDialog,
                onDismiss = { showForgotDialog = false },
                onSend = { emailInput ->
                    forgotLoading = true
                    viewModel.sendPasswordReset(emailInput)
                },
                isLoading = forgotLoading,
                message = forgotResult?.getOrNull()
            )
        }

        forgotResult?.let {
            if (!showForgotDialog) {
                Text(
                    it.getOrNull() ?: it.exceptionOrNull()?.message.orEmpty(),
                    color = if (it.isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
