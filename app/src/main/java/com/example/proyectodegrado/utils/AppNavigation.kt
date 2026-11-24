package com.example.proyectodegrado.utils

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.proyectodegrado.ui.screens.categories.CategoryViewModel
import com.example.proyectodegrado.ui.screens.home.HomeScreen
import com.example.proyectodegrado.ui.screens.home.HomeViewModel
import com.example.proyectodegrado.ui.screens.login.LoginScreen
import com.example.proyectodegrado.ui.screens.login.LoginViewModel
import com.example.proyectodegrado.ui.screens.products.AllProductsScreen
import com.example.proyectodegrado.ui.screens.products.ProductViewModel
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.profile.ProfileScreen
import com.example.proyectodegrado.ui.screens.profile.ProfileUiState
import com.example.proyectodegrado.ui.screens.profile.ProfileViewModel
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersViewModel
import com.example.proyectodegrado.ui.screens.purchases.PurchasesScreen
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import com.example.proyectodegrado.ui.screens.reports.ReportsScreen
import com.example.proyectodegrado.ui.screens.reports.ReportsViewModel
import com.example.proyectodegrado.ui.screens.role.RoleScreen
import com.example.proyectodegrado.ui.screens.role.RoleViewModel
import com.example.proyectodegrado.ui.screens.sales.SalesScreen
import com.example.proyectodegrado.ui.screens.sales.SalesViewModel
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleViewModel
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.store.StoreViewModel
import com.example.proyectodegrado.ui.screens.whatsapp_sales.WhatsappSalesScreen
import com.example.proyectodegrado.ui.screens.whatsapp_sales.WhatsappSalesViewModel
import com.example.proyectodegrado.ui.screens.workers.CreateWorkerScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appPrefs = remember { AppPreferences(context) }

    val dpSession by DependencyProvider.sessionState.collectAsState()
    val isLoggedIn = dpSession.authToken != null

    val profileViewModel: ProfileViewModel? = if (isLoggedIn) {
        appViewModel { DependencyProvider.provideProfileViewModel() }
    } else {
        null
    }
    val profileUi by profileViewModel?.ui?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(ProfileUiState()) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentTitle by rememberSaveable { mutableStateOf("Inicio") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routeAlias = mapOf(
        "employees" to "workers",
        "schedules" to "schedule",
        "stores" to "store"
    )

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            DependencyProvider.refreshMenu()
        }
    }

    LaunchedEffect(Unit) {
        if (DependencyProvider.isLoggedIn() && currentRoute == "login") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute == "login" || currentRoute == "register") {
            drawerState.close()
        }
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentTitle = determineTitle(backStackEntry.destination.route)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute != "login" && currentRoute != "register",
        drawerContent = {
            if (currentRoute != "login" && currentRoute != "register") {
                DrawerContent(
                    avatarUrl = profileUi.avatarUrl,
                    onItemSelected = { routeId ->
                        scope.launch {
                            if (routeId == "logout") {
                                drawerState.close()

                                DependencyProvider.clearCurrentSession()

                                Toast.makeText(
                                    context,
                                    "Sesión cerrada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                                return@launch
                            }

                            drawerState.close()

                            val target = routeAlias[routeId] ?: routeId
                            val current = currentRoute?.substringBefore("/")

                            if (target != current) {
                                navController.navigate(target) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
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
        }
    ) {
        val navBackStackEntry2 by navController.currentBackStackEntryAsState()
        val currentRoute2 = navBackStackEntry2?.destination?.route

        Scaffold(
            topBar = {
                if (shouldShowTopBar(currentRoute2)) {
                    TopAppBar(
                        title = {
                            val storeName = appPrefs.getStoreName()

                            Column {
                                Text(
                                    text = currentTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!storeName.isNullOrBlank()) {
                                    Text(
                                        text = storeName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            if (shouldShowBack(currentRoute2)) {
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
                startDestination = if (DependencyProvider.isLoggedIn()) "home" else "login",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                composable("login") {
                    val loginViewModel: LoginViewModel = appViewModel { DependencyProvider.provideLoginViewModel() }
                    LoginScreen(navController, loginViewModel)
                }
                composable("home") {
                    val homeViewModel: HomeViewModel = appViewModel { DependencyProvider.provideHomeViewModel() }
                    HomeScreen(homeViewModel, navController)
                }
                composable("categories") {
                    val categoryViewModel: CategoryViewModel = appViewModel { DependencyProvider.provideCategoryViewModel() }
                    CategoriesScreen(navController, categoryViewModel)
                }
                composable("sales") {
                    val salesViewModel: SalesViewModel = appViewModel { DependencyProvider.provideSalesViewModel() }
                    SalesScreen(navController, salesViewModel)
                }
                composable("purchases") {
                    PurchasesScreen(navController)
                }
                composable("reports") {
                    val reportsViewModel: ReportsViewModel = appViewModel { DependencyProvider.provideReportsViewModel() }
                    ReportsScreen(
                        navController = navController,
                        viewModel = reportsViewModel
                    )
                }
                composable("products") {
                    val productViewModel: ProductViewModel = appViewModel { DependencyProvider.provideProductViewModel() }
                    AllProductsScreen(navController, productViewModel)
                }
                composable("providers") {
                    val providerViewModel: ProvidersViewModel = appViewModel { DependencyProvider.provideProviderViewModel() }
                    ProvidersScreen(navController, providerViewModel)
                }

                composable(
                    route = "products/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    val productViewModel: ProductViewModel = appViewModel { DependencyProvider.provideProductViewModel() }
                    ProductsByCategoryScreen(navController, productViewModel, categoryId)
                }

                composable("store") {
                    val storeViewModel: StoreViewModel = appViewModel { DependencyProvider.provideStoreViewModel() }
                    StoreScreen(navController, storeViewModel)
                }
                composable("role") {
                    val roleViewModel: RoleViewModel = appViewModel { DependencyProvider.provideRoleViewModel() }
                    RoleScreen(navController, roleViewModel)
                }
                composable("workers") {
                    val workersViewModel: WorkersViewModel = appViewModel { DependencyProvider.provideWorkersViewModel() }
                    WorkersScreen(navController = navController, viewModel = workersViewModel)
                }
                composable("registerEmployee") {
                    val workersViewModel: WorkersViewModel = appViewModel { DependencyProvider.provideWorkersViewModel() }
                    val registerViewModel: RegisterViewModel = appViewModel { DependencyProvider.provideRegisterViewModel() }
                    CreateWorkerScreen(
                        navController = navController,
                        workersViewModel = workersViewModel,
                        registerViewModel = registerViewModel
                    )
                }
                composable("schedule") {
                    val scheduleViewModel: ScheduleViewModel = appViewModel { DependencyProvider.provideScheduleViewModel() }
                    ScheduleScreen(navController, scheduleViewModel)
                }

                composable("cash") {
                    val storeId = DependencyProvider.getCurrentStoreId()
                    val userId = DependencyProvider.getCurrentUserId()
                    CashScreen(
                        storeId = storeId,
                        userId = userId,
                        repository = DependencyProvider.provideCashRepository()
                    )
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
                        userId = be.arguments!!.getInt("userId"),
                        repository = DependencyProvider.provideCashRepository()
                    )
                }

                composable("whatsapp_sales") {
                    val whatsappSalesViewModel: WhatsappSalesViewModel = appViewModel { DependencyProvider.provideWhatsappSalesViewModel() }
                    WhatsappSalesScreen(navController, whatsappSalesViewModel)
                }

                composable("profile") {
                    val vm: ProfileViewModel = profileViewModel ?: appViewModel { DependencyProvider.provideProfileViewModel() }
                    ProfileScreen(viewModel = vm)
                }
            }
        }
    }
}
