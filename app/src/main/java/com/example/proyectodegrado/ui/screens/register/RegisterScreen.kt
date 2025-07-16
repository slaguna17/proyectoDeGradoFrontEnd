package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    // Input Field variables
    val logo = painterResource(R.drawable.logonobackground)
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

    LaunchedEffect(Unit) {
        viewModel.fetchRoles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Registrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text(text = "Nombre de usuario") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(text = "Correo Electronico") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Repetir Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text(text = "Nombre completo") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text(text = "Telefono") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text(text = "Fecha de nacimiento") })
        Spacer(modifier = Modifier.height(16.dp))

        RoleDropdown(viewModel = viewModel, roles = roles, selectedRole = selectedRole, onRoleSelected = { role ->
            selectedRole = role
        })
        Spacer(modifier = Modifier.height(16.dp))

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
            println("selectedRole?.id: ${selectedRole?.id}")
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                fullName = fullName,
                dateOfBirth = dateOfBirth,
                phone = phone,
                avatar = avatar.toString(),
                roleId = selectedRole?.id ?: 0 // Usa el ID del rol seleccionado
            )
            viewModel.registerUser(request,
                onSuccess = { navController -> // Recibe NavController
                    navController.navigate("home") // Navega a la pantalla de inicio
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

@Composable
fun RoleDropdown(
    viewModel: RegisterViewModel,
    roles: List<Role>,
    selectedRole: Role?,
    onRoleSelected: (Role) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedRole?.name ?: "Selecciona un rol",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    onClick = {
                        onRoleSelected(role)
                        viewModel.setSelectedRoleId(role.id)
                        expanded = false
                    },
                    text = { Text(text = role.name) }
                )
            }
        }
    }
}