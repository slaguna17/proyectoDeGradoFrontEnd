package com.example.proyectodegrado.ui.screens.workers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.RegisterWorkerRequest
import com.example.proyectodegrado.ui.components.StoreDropdown
import com.example.proyectodegrado.ui.components.ScheduleDropdown

@Composable
fun CreateWorkerScreen(
    navController: NavController,
    viewModel: WorkersViewModel
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val stores by viewModel.stores.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val roles = listOf(
        Role(
            id = 2, name = "Empleado", isAdmin = false,
            description = "hola"
        )
    )
    var selectedRole by remember { mutableStateOf<Role?>(roles.firstOrNull()) }
    var selectedStoreId by remember { mutableStateOf<Int?>(null) }
    var selectedScheduleId by remember { mutableStateOf<Int?>(null) }

    // Carga tiendas y horarios al entrar
    LaunchedEffect(Unit) {
//        viewModel.loadStoresAndSchedules()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 80.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logonobackground),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        Text("Nuevo Empleado", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repetir Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Fecha de nacimiento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Dropdown de tienda
        StoreDropdown(
            stores = stores,
            selectedStoreId = selectedStoreId,
            onStoreSelected = { selectedStoreId = it }
        )
        Spacer(Modifier.height(8.dp))

        // Dropdown de turno
        ScheduleDropdown(
            schedules = schedules,
            selectedScheduleId = selectedScheduleId,
            onScheduleSelected = { selectedScheduleId = it }
        )
        Spacer(Modifier.height(8.dp))

        // Si quieres permitir elegir rol, puedes poner un dropdown aquí
        // Si siempre será "Empleado", selecciona por defecto

        if (errorMessage.isNotBlank()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = {
            errorMessage = ""
            when {
                password != repeatPassword -> {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }
                selectedRole == null -> {
                    errorMessage = "Debes seleccionar un rol"
                    return@Button
                }
                selectedStoreId == null -> {
                    errorMessage = "Debes seleccionar una tienda"
                    return@Button
                }
                selectedScheduleId == null -> {
                    errorMessage = "Debes seleccionar un horario"
                    return@Button
                }
            }

            val request = RegisterWorkerRequest(
                username = username,
                email = email,
                password = password,
                fullName = fullName,
                phone = phone,
                storeId = selectedStoreId!!,
                scheduleId = selectedScheduleId!!,
                roleId = selectedRole!!.id
            )

            viewModel.registerWorker(
                request,
                onSuccess = { navController.navigate("workers") },
                onError = { errorMessage = it }
            )
        }) {
            Text("Registrar Empleado")
        }
    }
}
