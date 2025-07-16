package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Role

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {

    // State variables
    var errorMessage by remember { mutableStateOf("") }

    //Image
    val logo = painterResource(R.drawable.logonobackground)

    // Input Field variables
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Uri?>(null) }

    // Roles
    val roles by viewModel.roles.collectAsState()
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Registrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Correo Electronico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Repetir Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text(text = "Fecha de nacimiento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

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
        Spacer(modifier = Modifier.height(16.dp))

        if (showRoleError) {
            Text(
                text = "Debes seleccionar un rol",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp, top = 2.dp)
            )
        }

//        uploadImage(
//            buttonText = "Elegir foto de categoria",
//            onUploadResult = { result ->
//                result.fold(
//                    onSuccess = { url -> onImageChange(url) },
//                    onFailure = { error ->
//                        // Aquí puedes mostrar un mensaje de error o registrar la falla.
//                        onImageChange("")  // O mantener el campo vacío
//                    }
//                )
//            }
//        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (password != repeatPassword) {
                errorMessage = "Contraseñas no coinciden"
                return@Button
            }
            if (selectedRole == null) {
                showRoleError = true
                errorMessage = "Debes seleccionar un rol"
                return@Button
            }
            println("selectedRole?.id: ${selectedRole?.id}")
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                fullName = fullName,
                dateOfBirth = dateOfBirth,
                phone = phone,
                avatar = avatar.toString(),
                roleId = selectedRole?.id ?: 0
            )
            viewModel.registerUser(request,
                onSuccess = { navController ->
                    navController.navigate("home")
                },
                onError = { errorMessage = it },
                navController = navController
            )
        }) {
            Text(text = "Registrarme")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "¿Tienes cuenta? ")
            Text(
                text = "¡Ingresa ahora!",
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}