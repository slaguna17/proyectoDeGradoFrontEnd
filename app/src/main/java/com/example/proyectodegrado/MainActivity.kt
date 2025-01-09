package com.example.proyectodegrado

import ApiService
import LoginViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.repository.AuthRepository
import com.example.proyectodegrado.ui.screens.TestAPI.GetAllUsers
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.theme.ProyectoDeGradoTheme
import retrofit2.Retrofit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoDeGradoTheme {
                MyApp()
//                RegisterScreen()
//                LoginScreen()
//                GetAllUsers()
//                HomeScreen(username = "Sergio", onSettingsClicked = { /*TODO*/ }) {
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

@Composable
fun MyApp(){
    val navController = rememberNavController()
    val api = RetrofitClient.apiService
    val authRepository = AuthRepository(apiService = api)
    val loginViewModel = LoginViewModel(authRepository)
    NavHost(navController, startDestination = "login"){
        composable("login"){ LoginScreen(navController = navController, loginViewModel)}
        composable("home"){ HomeScreen(username = "Sergio", onSettingsClicked = { /*TODO*/ }) {} }
        composable("register"){ RegisterScreen(navController = navController)}
        composable("testAPI"){ GetAllUsers()}
    }
}




