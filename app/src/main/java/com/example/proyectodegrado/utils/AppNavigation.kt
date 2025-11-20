package com.example.proyectodegrado.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider
import com.example.proyectodegrado.ui.components.DrawerContent
import com.example.proyectodegrado.ui.screens.cash.CashScreen
import com.example.proyectodegrado.ui.screens.categories.CategoriesScreen
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.products.AllProductsScreen
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.profile.ProfileScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.CreateWorkerScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import com.example.proyectodegrado.ui.screens.purchases.PurchasesScreen
import com.example.proyectodegrado.ui.screens.role.RoleScreen
import com.example.proyectodegrado.ui.screens.sales.SalesScreen
import com.example.proyectodegrado.ui.screens.whatsapp_sales.WhatsappSalesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appPrefs = remember { AppPreferences(context) }

    val loginViewModel = remember { DependencyProvider.provideLoginViewModel() }
    val registerViewModel = remember { DependencyProvider.provideRegisterViewModel() }
    val categoryViewModel = remember { DependencyProvider.provideCategoryViewModel() }
    val productViewModel = remember { DependencyProvider.provideProductViewModel() }
    val storeViewModel = remember { DependencyProvider.provideStoreViewModel() }
    val roleViewModel = remember { DependencyProvider.provideRoleViewModel() }
    val scheduleViewModel = remember { DependencyProvider.provideScheduleViewModel() }
    val workersViewModel = remember { DependencyProvider.provideWorkersViewModel() }
    val profileViewModel = remember { DependencyProvider.provideProfileViewModel() }
    val sessionViewModel = remember { DependencyProvider.provideSessionViewModel() }
    val providerViewModel = remember { DependencyProvider.provideProviderViewModel() }
    val salesViewModel = remember { DependencyProvider.provideSalesViewModel() }
    val whatsappSalesViewModel = remember { DependencyProvider.provideWhatsappSalesViewModel() }
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val dpSession by DependencyProvider.sessionState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentTitle by rememberSaveable { mutableStateOf("Inicio") }
    // Estado local para el nombre de la tienda
    var currentStoreName by rememberSaveable { mutableStateOf(appPrefs.getStoreName()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val profileUi by profileViewModel.ui.collectAsStateWithLifecycle()

    val routeAlias = mapOf(
        "employees" to "workers",
        "schedules" to "schedule",
        "stores"    to "store"
    )

    // Actualiza el título y el nombre de la tienda cada vez que cambia la navegación
    LaunchedEffect(navBackStackEntry) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentTitle = determineTitle(backStackEntry.destination.route)
            // Leemos de nuevo las preferencias por si cambiaron en la pantalla de Tiendas
            currentStoreName = appPrefs.getStoreName()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                avatarUrl = profileUi.avatarUrl,
                onItemSelected = { routeId ->
                    scope.launch {
                        drawerState.close()
                        val target = routeAlias[routeId] ?: routeId

                        val current = currentRoute?.substringBefore("/")
                        if (target != current) {
                            navController.navigate(target) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                isAdmin = dpSession.isAdmin,
                menu = dpSession.menu
            )
        }
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (shouldShowTopBar(currentRoute)) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = currentTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!currentStoreName.isNullOrBlank()) {
                                    Text(
                                        text = currentStoreName!!,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
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
                composable("login") { LoginScreen(navController, loginViewModel) }
                composable("home") { HomeScreen() }
                composable("categories"){ CategoriesScreen(navController, categoryViewModel) }
                composable("sales") { SalesScreen(navController) }
                composable("purchases") { PurchasesScreen(navController) }
                composable("products") { AllProductsScreen(navController, productViewModel) }
                composable("providers") { ProvidersScreen(navController, providerViewModel) }

                composable(
                    route = "products/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    ProductsByCategoryScreen(navController, productViewModel, categoryId)
                }

                composable("store")   { StoreScreen(navController, storeViewModel) }
                composable("role")   { RoleScreen(navController, roleViewModel) }
                composable("workers") { WorkersScreen(navController = navController, viewModel = workersViewModel) }
                composable("registerEmployee") { CreateWorkerScreen(navController = navController, workersViewModel = workersViewModel, registerViewModel = registerViewModel) }
                composable("schedule"){ ScheduleScreen(navController, scheduleViewModel) }

                composable("cash") {
                    val storeId = DependencyProvider.getCurrentStoreId()
                    val userId  = DependencyProvider.getCurrentUserId()
                    CashScreen(storeId = storeId, userId = userId)
                }
                composable(
                    route = "cash/{storeId}/{userId}",
                    arguments = listOf(
                        navArgument("storeId") { type = NavType.IntType },
                        navArgument("userId") { type = NavType.IntType }
                    )
                ) { be ->
                    CashScreen(
                        storeId = be.arguments!!.getInt("storeId"),
                        userId  = be.arguments!!.getInt("userId")
                    )
                }

                composable("whatsapp_sales") { WhatsappSalesScreen(navController, whatsappSalesViewModel)}
                composable("settings")  { SettingsScreen(navController) }
                composable("profile")   { ProfileScreen(viewModel = profileViewModel) }
            }
        }
    }
}