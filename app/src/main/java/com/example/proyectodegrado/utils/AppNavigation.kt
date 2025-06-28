package com.example.proyectodegrado.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectodegrado.di.DependencyProvider
import com.example.proyectodegrado.ui.components.DrawerContent
import com.example.proyectodegrado.ui.screens.balance.BalanceScreen
import com.example.proyectodegrado.ui.screens.barcode.BarcodeScreen
import com.example.proyectodegrado.ui.screens.categories.CategoriesScreen
import com.example.proyectodegrado.ui.screens.forecast.ForecastScreen
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.CreateWorkerScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import kotlinx.coroutines.launch

// La función determineTitle ahora usa "categories" para más claridad
fun determineTitle(route: String?, categoryName: String? = null): String {
    return when (route) {
        "home" -> "Inicio"
        "categories" -> "Categorías" // Ruta para la pantalla de categorías
        "products/{categoryId}" -> categoryName ?: "Productos"
        "store" -> "Tienda"
        "workers" -> "Empleados"
        "schedule" -> "Horarios"
        "forecast" -> "Pronósticos"
        "balance" -> "Caja"
        "providers" -> "Proveedores"
        "barcode" -> "Código de Barras"
        "settings" -> "Ajustes"
        "login", "register" -> ""
        "registerEmployee" -> "Nuevo Empleado"
        else -> "TuKiosco"
    }
}

fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}

fun shouldShowBack(route: String?): Boolean {
    return route == "products/{categoryId}" || route == "registerEmployee"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Los ViewModels se crean aquí, como fuente única de verdad.
    val loginViewModel = DependencyProvider.provideLoginViewModel()
    val registerViewModel = DependencyProvider.provideRegisterViewModel()
    val categoryViewModel = DependencyProvider.provideCategoryViewModel()
    val productViewModel = DependencyProvider.provideProductViewModel() // <-- Instancia única
    val storeViewModel = DependencyProvider.provideStoreViewModel()
    val providerViewModel = DependencyProvider.provideProviderViewModel()
    val scheduleViewModel = DependencyProvider.provideScheduleViewModel()
    val workersViewModel = DependencyProvider.provideWorkersViewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTitle by rememberSaveable { mutableStateOf("Inicio") }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentTitle = determineTitle(backStackEntry.destination.route)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onItemSelected = { screenLabel ->
                    val route = when (screenLabel) {
                        "Inicio" -> "home"
                        "Categorías" -> "categories" // Apunta a la ruta correcta de categorías
                        "Tienda" -> "store"
                        "Empleados" -> "workers"
                        "Horarios" -> "schedule"
                        "Pronósticos" -> "forecast"
                        "Caja" -> "balance"
                        "Proveedores" -> "providers"
                        "Código de barras" -> "barcode"
                        "Ajustes" -> "settings"
                        else -> null
                    }
                    scope.launch {
                        drawerState.close()
                        route?.let {
                            navController.navigate(it) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (shouldShowTopBar(currentRoute)) {
                    TopAppBar(
                        title = { Text(currentTitle) },
                        navigationIcon = {
                            if (shouldShowBack(currentRoute)) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                                }
                            } else {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                composable("login") { LoginScreen(navController = navController, viewModel = loginViewModel) }
                composable("register") { RegisterScreen(navController = navController, viewModel = registerViewModel) }

                // --- INICIO DE LA CORRECIÓN ---
                composable("home") {
                    // Ahora le pasamos la instancia única del ViewModel
                    HomeScreen(navController = navController, viewModel = productViewModel)
                }
                composable("categories") { // La ruta de Categorías
                    CategoriesScreen(navController = navController, viewModel = categoryViewModel)
                }
                // --- FIN DE LA CORRECIÓN ---

                composable(
                    route = "products/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    ProductsByCategoryScreen(
                        navController = navController,
                        viewModel = productViewModel,
                        categoryId = categoryId
                    )
                }
                composable("store") { StoreScreen(navController = navController, viewModel = storeViewModel) }
                composable("workers") { WorkersScreen(navController = navController, viewModel = workersViewModel) }
                composable("registerEmployee") { CreateWorkerScreen(navController = navController, viewModel = registerViewModel) }
                composable("schedule") { ScheduleScreen(navController = navController, viewModel = scheduleViewModel) }
                composable("forecast") { ForecastScreen(navController = navController) }
                composable("balance") { BalanceScreen(navController = navController) }
                composable("providers") { ProvidersScreen(navController = navController, viewModel = providerViewModel) }
                composable("barcode") { BarcodeScreen(navController = navController) }
                composable("settings") { SettingsScreen(navController = navController) }
            }
        }
    }
}