package com.example.proyectodegrado

import ApiService
import HomeScreen
import LoginViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.repository.AuthRepository
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
import retrofit2.Retrofit

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
    val navController = rememberNavController()
    val api = RetrofitClient.apiService
    val authRepository = AuthRepository(apiService = api)
    val loginViewModel = LoginViewModel(authRepository)
    NavHost(navController, startDestination = "home"){
        composable("login"){ LoginScreen(navController = navController, loginViewModel)}
        composable("home"){ HomeScreen(navController = navController)  }
        composable("register"){ RegisterScreen(navController = navController)}
        composable("testAPI"){ GetAllUsers()}
        composable("products"){ ProductsScreen(navController = navController)}
        composable("store"){ StoreScreen(navController = navController) }
        composable("workers"){ WorkersScreen(navController = navController) }
        composable("forecast"){ ForecastScreen(navController = navController) }
        composable("balance"){ BalanceScreen(navController = navController) }
        composable("barcode"){ BarcodeScreen(navController = navController)}
        composable("settings"){ SettingsScreen(navController = navController)}

    }
}





