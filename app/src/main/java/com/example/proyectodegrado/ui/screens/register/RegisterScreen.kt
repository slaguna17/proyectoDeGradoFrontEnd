package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navController: NavController
) {
    val ui by viewModel.ui.collectAsState()
    val registerState by viewModel.registerState.observeAsState(initial = RegisterState.Idle)
    // val roles by viewModel.roles.collectAsState() // Descomenta si tienes roles dinámicos

    // Este LaunchedEffect es ahora mucho más simple. Solo reacciona al estado.
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
        if (registerState is RegisterState.Error) {
            // Aquí podrías mostrar un Snackbar o Toast con el mensaje de error
            val errorMessage = (registerState as RegisterState.Error).message
            // p. ej. scaffoldState.snackbarHostState.showSnackbar(errorMessage)
        }
    }

    val logo = painterResource(R.drawable.logonobackground)
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onPickAvatar(uri)
    }
    var showDatePicker by remember { mutableStateOf(false) }

    // El resto de la UI es prácticamente igual...
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp) // Ajuste de padding para mejor visualización
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (Todos tus TextFields, Buttons, etc. se mantienen igual)
        // Por ejemplo:
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Regístrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.fullName,
            onValueChange = viewModel::onFullName,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        // ... Y así con todos los demás campos ...
        Spacer(Modifier.height(10.dp))
        // ...

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

        // ...
    }
}