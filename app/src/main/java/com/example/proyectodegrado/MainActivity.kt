package com.example.proyectodegrado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoDeGradoTheme {
                LoginScreen()
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun preview(){
    ProyectoDeGradoTheme {
        LoginScreen()
    }
}




