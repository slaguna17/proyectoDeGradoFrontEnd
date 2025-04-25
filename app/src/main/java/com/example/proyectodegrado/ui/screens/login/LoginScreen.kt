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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

//@Preview (showBackground = true)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel){

    //State Variables
    val loginState by viewModel.loginState.observeAsState()
    var isLoading by remember { mutableStateOf(false) }

    //Images
    val logo = painterResource(R.drawable.logonobackground)
    val googleLogo= painterResource(R.drawable.google_logo)
    val facebookLogo = painterResource(id = R.drawable.facebook_logo)

    //Screen variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))

        Text(text = "¡Bienvenido!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Ingresa a tu cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = { Text("Correo Electronico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                viewModel.login(email, password)
            }
        ){
            if (isLoading){
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
                    navController.navigate("home")
                }
                result.isFailure -> {
                    isLoading = false
                    Text("Login failed: ${result.exceptionOrNull()?.message}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row (verticalAlignment = Alignment.CenterVertically){
            Checkbox(checked = false, onCheckedChange = {/* TODO */})
            Text(text = "Recuerdame")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /*TODO*/ }) {
            Text(text = "¿Olvidaste tu contraseña?")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Ingresar con: ")
        Spacer(modifier = Modifier.height(8.dp))

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ){
            Image(painter = googleLogo, contentDescription = "Google Logo", modifier = Modifier
                .size(60.dp)
                .clickable { })
            Image(painter = facebookLogo, contentDescription = "Google Logo", modifier = Modifier
                .size(60.dp)
                .clickable { })
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
    }
}

