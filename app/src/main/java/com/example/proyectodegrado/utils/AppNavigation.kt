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
import androidx.compose.runtime.collectAsState
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
import com.example.proyectodegrado.ui.screens.barcode.BarcodeScreen
import com.example.proyectodegrado.ui.screens.cash.CashScreen
import com.example.proyectodegrado.ui.screens.categories.CategoriesScreen
import com.example.proyectodegrado.ui.screens.forecast.ForecastScreen
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.products.AllProductsScreen
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.profile.ProfileScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.CreateWorkerScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val loginViewModel = DependencyProvider.provideLoginViewModel()
    val registerViewModel = DependencyProvider.provideRegisterViewModel()
    val categoryViewModel = DependencyProvider.provideCategoryViewModel()
    val productViewModel = DependencyProvider.provideProductViewModel()
    val storeViewModel = DependencyProvider.provideStoreViewModel()
    val providerViewModel = DependencyProvider.provideProviderViewModel()
    val scheduleViewModel = DependencyProvider.provideScheduleViewModel()
    val workersViewModel = DependencyProvider.provideWorkersViewModel()
    val profileViewModel = DependencyProvider.provideProfileViewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTitle by rememberSaveable { mutableStateOf("Inicio") }
    val currentUser = profileViewModel.user.collectAsState().value

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
                        "Productos" -> "products"
                        "Categorías" -> "categories"
                        "Tienda" -> "store"
                        "Empleados" -> "workers"
                        "Horarios" -> "schedule"
                        "Pronósticos" -> "forecast"
                        "Caja" -> "cash"
                        "Proveedores" -> "providers"
                        "Código de barras" -> "barcode"
                        "Ajustes" -> "settings"
                        "profile" -> "profile"
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
                },
                avatarUrl = currentUser?.avatarUrl ?: currentUser?.avatar
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
                composable("home") { HomeScreen() }
                composable("categories") { CategoriesScreen(navController= navController, viewModel = categoryViewModel) }
                composable("products") { AllProductsScreen(navController = navController, viewModel = productViewModel) }

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
                composable("registerEmployee") { CreateWorkerScreen(navController = navController, viewModel = workersViewModel) }
                composable("schedule") { ScheduleScreen(navController = navController, viewModel = scheduleViewModel) }
                composable("forecast") { ForecastScreen(navController = navController) }

                // ✅ NUEVO: destino simple "cash" (sin args).
                // Toma userId/storeId de la sesión (o 1/1 si no hay).
                composable("cash") {
                    val storeId = DependencyProvider.getCurrentStoreId()
                    val userId  = DependencyProvider.getCurrentUserId()
                    CashScreen(storeId = storeId, userId = userId)
                }

                // (Opcional) Mantén también la variante con argumentos si en algún flujo los pasas explícitos:
                composable(
                    route = "cash/{storeId}/{userId}",
                    arguments = listOf(
                        navArgument("storeId") { type = NavType.IntType },
                        navArgument("userId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val storeId = backStackEntry.arguments!!.getInt("storeId")
                    val userId = backStackEntry.arguments!!.getInt("userId")
                    CashScreen(storeId = storeId, userId = userId)
                }

                composable("providers") { ProvidersScreen(navController = navController, viewModel = providerViewModel) }
                composable("barcode") { BarcodeScreen(navController = navController) }
                composable("settings") { SettingsScreen(navController = navController) }
                composable("profile") { ProfileScreen(navController = navController, viewModel = profileViewModel) }
            }
        }
    }
}
