package com.example.proyectodegrado.ui.screens.register

import RegisterRequest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.ui.components.uploadImage
import com.example.proyectodegrado.ui.screens.TestAPI.UserViewModel

val registerVM = RegisterViewModel()
//@Preview(showBackground = true)
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = registerVM){

    val viewModel : RegisterViewModel = viewModel()
    var errorMessage by remember { mutableStateOf("") }

    val logo = painterResource(R.drawable.logonobackground)
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf<Uri?>(null) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Registrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = username, onValueChange = { username = it}, label = { Text(text = "Nombre de usuario") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it}, label = { Text(text = "Correo Electronico") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = {repeatPassword = it},
            visualTransformation = PasswordVisualTransformation(),
            label = { Text(text = "Repetir Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it}, label = { Text(text = "Nombre completo") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it}, label = { Text(text = "Telefono") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it}, label = { Text(text = "Fecha de nacimiento") })
        Spacer(modifier = Modifier.height(16.dp))

        uploadImage(buttonText = "Elegir foto de perfil")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (password != repeatPassword) {
                errorMessage = "Contraseñas no coinciden"
                return@Button
            }
            val request = RegisterRequest(
                username = username,
                email = email,
                password = password,
                full_name = fullName, // Replace with input if needed
                date_of_birth = dateOfBirth, // Replace with input if needed
                phone = phone,
                avatar = avatar.toString()
            )
            viewModel.registerUser(request,
                onSuccess = { errorMessage = "Registration successful!" },
                onError = { errorMessage = it }
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
