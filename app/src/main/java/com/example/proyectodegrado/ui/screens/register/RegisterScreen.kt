package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodegrado.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navController: NavController
) {
    val ui by viewModel.ui.collectAsState()
    val registerState by viewModel.registerState.observeAsState(initial = RegisterState.Idle)

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
        if (registerState is RegisterState.Error) {
            val errorMessage = (registerState as RegisterState.Error).message
        }
    }

    val logo = painterResource(R.drawable.logonobackground)
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onPickAvatar(uri)
    }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Regístrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.fullName,
            onValueChange = viewModel::onFullName,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { viewModel.register() },
            enabled = !ui.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ui.loading) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Crear cuenta")
            }
        }
    }
}