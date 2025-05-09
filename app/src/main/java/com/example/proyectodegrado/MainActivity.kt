package com.example.proyectodegrado

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoDeGradoTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(){
    AppNavigation()
}





