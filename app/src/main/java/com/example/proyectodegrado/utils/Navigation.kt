package com.example.proyectodegrado.utils

import HomeScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val loginViewModel = DependencyProvider.provideLoginViewModel()
    val registerViewModel = DependencyProvider.provideRegisterViewModel()
    val productViewModel = DependencyProvider.provideProductViewModel()
    val storeViewModel = DependencyProvider.provideStoreViewModel()
    val providerViewModel = DependencyProvider.provideProviderViewModel()
    val scheduleViewModel = DependencyProvider.provideScheduleViewModel()

    NavHost(navController, startDestination = "login"){
        composable("login"){ LoginScreen(navController = navController, loginViewModel) }
        composable("home"){ HomeScreen(navController = navController)  }
        composable("register"){ RegisterScreen(navController = navController, registerViewModel) }
        composable("testAPI"){ GetAllUsers() }

        composable("products"){ ProductsScreen(navController = navController, productViewModel) }
        composable(
            route = "products/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
            ProductsByCategoryScreen(navController, productViewModel, categoryId)
        }
        composable("store"){ StoreScreen(navController = navController, storeViewModel) }
        composable("workers"){ WorkersScreen(navController = navController) }
        composable("schedule"){ ScheduleScreen(navController = navController, scheduleViewModel) }
        composable("forecast"){ ForecastScreen(navController = navController) }
        composable("balance"){ BalanceScreen(navController = navController) }
        composable("providers"){ ProvidersScreen(navController = navController, providerViewModel) }
        composable("barcode"){ BarcodeScreen(navController = navController) }
        composable("settings"){ SettingsScreen(navController = navController) }

    }
}