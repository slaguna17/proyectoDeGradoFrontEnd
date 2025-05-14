import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.example.proyectodegrado.ui.screens.products.AllProductsScreen
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import kotlinx.coroutines.launch

// Función auxiliar para determinar el título basado en la ruta (adaptada)
fun determineTitle(route: String?, categoryName: String? = null): String {
    return when (route) {
        "home" -> "Inicio"
        "products" -> "Categorías" // Ruta "products" ahora es CategoriesScreen
        "products/{categoryId}" -> categoryName ?: "Productos" // Título para productos de una categoría
        "store" -> "Tienda"
        "workers" -> "Empleados"
        "schedule" -> "Horarios"
        "forecast" -> "Pronósticos"
        "balance" -> "Caja"
        "providers" -> "Proveedores"
        "barcode" -> "Código de Barras"
        "settings" -> "Ajustes"
        "testAPI" -> "Test API"
        "login", "register" -> "" // Sin título en TopAppBar para estas
        else -> "TuKiosco" // Título por defecto
    }
}

// Función auxiliar para decidir si mostrar la TopAppBar estándar
fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Inyección de ViewModels (de la versión nueva)
    val loginViewModel = DependencyProvider.provideLoginViewModel()
    val registerViewModel = DependencyProvider.provideRegisterViewModel()
    val categoryViewModel = DependencyProvider.provideCategoryViewModel() // Para CategoriesScreen
    val productViewModel = DependencyProvider.provideProductViewModel() // Para ProductsByCategoryScreen
    val storeViewModel = DependencyProvider.provideStoreViewModel()
    val providerViewModel = DependencyProvider.provideProviderViewModel()
    val scheduleViewModel = DependencyProvider.provideScheduleViewModel()
    val workersViewModel = DependencyProvider.provideWorkersViewModel()

    // Estado para el Drawer y CoroutineScope (de la versión antigua)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado para guardar el título actual (de la versión antigua)
    var currentTitle by rememberSaveable { mutableStateOf("Inicio") }
    var categoryNameForTitle by rememberSaveable { mutableStateOf<String?>(null) }


    // Escuchar cambios de navegación para actualizar el título (de la versión antigua)
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry?.destination?.route
            // Si estás en la pantalla de productos por categoría, podrías querer pasar el nombre de la categoría
            // Esto es un ejemplo, necesitarías obtener el nombre de la categoría desde el ViewModel o argumentos
            if (route == "products/{categoryId}") {
                // Aquí deberías obtener el nombre de la categoría basado en categoryId
                // Por ahora, usaremos un placeholder o lo dejaremos como "Productos" si no se puede obtener.
                // categoryNameForTitle = productViewModel.getCategoryNameById(backStackEntry.arguments?.getInt("categoryId"))
                currentTitle = determineTitle(route, "Nombre Categoría") // Ejemplo
            } else {
                currentTitle = determineTitle(route)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // Opcional
        drawerContent = {
            DrawerContent(
                onItemSelected = { screenLabel ->
                    val route = when (screenLabel) {
                        "Inicio" -> "home"
                        "Categorías" -> "products" // "Productos" en el drawer ahora lleva a la ruta "products" (CategoriesScreen)
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
        Scaffold(
            topBar = {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                if (shouldShowTopBar(currentRoute)) {
                    TopAppBar(
                        title = { Text(currentTitle) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                            }
                        }
                        // Puedes añadir actions aquí si son globales
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login", // O la pantalla de inicio que prefieras post-login
                modifier = Modifier
                    .padding(innerPadding) // Aplicar padding del Scaffold
                    .fillMaxSize() // Asegura que el NavHost ocupe el espacio
            ) {
                composable("login") {
                    LoginScreen(navController = navController, viewModel = loginViewModel)
                }
                composable("register") {
                    RegisterScreen(navController = navController, viewModel = registerViewModel)
                }
                composable("home") {
                    HomeScreen(navController = navController)
                }
                // Ruta "products" ahora es para CategoriesScreen
                composable("products") {
                    CategoriesScreen(navController = navController, viewModel = categoryViewModel)
                }
                composable(
                    route = "products/{categoryId}", // Para productos específicos de una categoría
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    // Aquí es donde el productViewModel se usa para la pantalla de productos por categoría
                    ProductsByCategoryScreen(
                        navController = navController,
                        viewModel = productViewModel,
                        categoryId = categoryId
                    )
                }
                composable("store") {
                    StoreScreen(navController = navController, viewModel = storeViewModel)
                }
                composable("workers") {
                    WorkersScreen(
                        workersViewModel = workersViewModel,
                        // storeViewModel = storeViewModel, // Descomentar si se usa
                        // scheduleViewModel = scheduleViewModel, // Descomentar si se usa
                        onNavigateToCreateWorker = { /* Lógica para mostrar diálogo o navegar */ },
                        onNavigateToEditWorker = { employeeId -> /* Lógica para mostrar diálogo o navegar con employeeId */ }
                    )
                }
                composable("schedule") {
                    ScheduleScreen(navController = navController, viewModel = scheduleViewModel)
                }
                composable("forecast") {
                    ForecastScreen(navController = navController)
                }
                composable("balance") {
                    BalanceScreen(navController = navController)
                }
                composable("providers") {
                    ProvidersScreen(navController = navController, viewModel = providerViewModel)
                }
                composable("barcode") {
                    BarcodeScreen(navController = navController)
                }
                composable("settings") {
                    SettingsScreen(navController = navController)
                }
                composable("all_products") {
                    AllProductsScreen(navController = navController, viewModel = productViewModel)
                }
            }
        }
    }
}
