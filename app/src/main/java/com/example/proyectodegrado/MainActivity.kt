package com.example.proyectodegrado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodegrado.ui.screens.TestAPI.GetAllUsers
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoDeGradoTheme {
//                RegisterScreen()
                LoginScreen()
//                GetAllUsers()
                HomeScreen(username = "Sergio", onSettingsClicked = { /*TODO*/ }) {
                    
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun preview(){
    ProyectoDeGradoTheme {
        GetAllUsers()
    }
}




