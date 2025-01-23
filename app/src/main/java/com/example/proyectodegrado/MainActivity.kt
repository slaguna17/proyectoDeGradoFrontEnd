package com.example.proyectodegrado

import HomeScreen
import LoginViewModel
import UserService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.di.DependencyProvider
import com.example.proyectodegrado.ui.screens.TestAPI.GetAllUsers
import com.example.proyectodegrado.ui.screens.balance.BalanceScreen
import com.example.proyectodegrado.ui.screens.barcode.BarcodeScreen
import com.example.proyectodegrado.ui.screens.forecast.ForecastScreen

import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.products.ProductsScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme
import com.example.proyectodegrado.utils.Navigation

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
    Navigation()
}





