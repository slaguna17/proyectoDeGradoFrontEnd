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
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import com.example.proyectodegrado.ui.screens.register.RoleDropdown

@Composable
fun CreateWorkerScreen(navController: NavController, viewModel: RegisterViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val roles by viewModel.roles.collectAsState()
//    val filteredRoles = roles.filter { !it.isAdmin }
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var showRoleError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchRoles()
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

        RoleDropdown(
            viewModel = viewModel,
            roles = roles,
            selectedRole = selectedRole,
            onRoleSelected = {
                selectedRole = it
                showRoleError = false
            },
            isError = showRoleError
        )
        Spacer(Modifier.height(16.dp))

        if (errorMessage.isNotBlank()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = {
            if (password != repeatPassword) {
                errorMessage = "Las contraseñas no coinciden"
                return@Button
            }
            if (selectedRole == null) {
                showRoleError = true
                errorMessage = "Debes seleccionar un rol"
                return@Button
            }
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                fullName = fullName,
                dateOfBirth = dateOfBirth,
                phone = phone,
                avatar = "",
                roleId = selectedRole!!.id
            )
            viewModel.registerUser(request,
                onSuccess = { navController ->
                    navController.navigate("home")
                },
                onError = { errorMessage = it },
                navController = navController
            )
        }) {
            Text("Registrar Empleado")
        }
    }
}
