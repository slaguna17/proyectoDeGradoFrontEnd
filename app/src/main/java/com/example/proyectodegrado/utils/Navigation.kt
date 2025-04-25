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
import com.example.proyectodegrado.ui.screens.register.RegisterScreen
import com.example.proyectodegrado.ui.screens.settings.SettingsScreen
import com.example.proyectodegrado.ui.screens.store.StoreScreen
import com.example.proyectodegrado.ui.screens.workers.WorkersScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.example.proyectodegrado.ui.screens.products.CategoriesScreen
import com.example.proyectodegrado.ui.screens.products.ProductsByCategoryScreen
import com.example.proyectodegrado.ui.screens.providers.ProvidersScreen
import com.example.proyectodegrado.ui.screens.schedule.ScheduleScreen
// --- Imports necesarios ---
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable // Para recordar el estado del título
import androidx.navigation.compose.* // Para rememberNavController, NavHost, etc.
import androidx.navigation.NavHostController // Tipo explícito para claridad
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu


// ... otros imports (iconos, etc.)
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Si tienes muchos items y necesitas scroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.* // Asegúrate de usar Material 3

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.R // Asegúrate que la ruta a tus R sea correcta

// Este composable define el contenido completo del Drawer
@OptIn(ExperimentalMaterial3Api::class) // Necesario para ListItem de M3
@Composable
fun DrawerContent(onItemSelected: (String) -> Unit) {
    // Carga tus recursos (iconos, imagen de perfil)
    // Asegúrate que estos drawables existan en tu proyecto res/drawable
    val profilePic = painterResource(id = R.drawable.lemon_drink) // Reemplaza con tu imagen
    val homeIcon = ImageVector.vectorResource(id = R.drawable.home)
    val settingsIcon = ImageVector.vectorResource(id = R.drawable.settings)
    val workersIcon = ImageVector.vectorResource(id = R.drawable.group)
    val scheduleIcon = ImageVector.vectorResource(id = R.drawable.schedule)
    val forecastIcon = ImageVector.vectorResource(id = R.drawable.bar_chart)
    val productsIcon = ImageVector.vectorResource(id = R.drawable.products)
    val storeIcon = ImageVector.vectorResource(id = R.drawable.store)
    val balanceIcon = ImageVector.vectorResource(id = R.drawable.wallet)
    val providerIcon = ImageVector.vectorResource(id = R.drawable.truck)
    val barcodeIcon = ImageVector.vectorResource(id = R.drawable.barcode_scanner)

    // ModalDrawerSheet es el contenedor recomendado dentro de ModalNavigationDrawer
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight() // Ocupa toda la altura disponible
                .padding(16.dp) // Padding general
        ) {
            // Cabecera del Drawer (Nombre de la App y Avatar)
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Espacio antes del divisor
            ){
                Text(
                    text = "TuKiosco", // Nombre de tu app
                    style = MaterialTheme.typography.titleLarge // Estilo de título M3
                )
                // Puedes hacer el IconButton clickeable si lleva a un perfil, etc.
                IconButton(onClick = { /* Acción de clic en avatar (opcional) */ }) {
                    Image(
                        painter = profilePic,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape) // Hace la imagen circular
                    )
                }
            }

            HorizontalDivider() // Divisor estándar de Material 3

            Spacer(modifier = Modifier.height(16.dp)) // Espacio después del divisor

            // Lista de Items del Drawer
            // Si tienes muchos items, considera usar LazyColumn en lugar de Column aquí
            // LazyColumn { items(listaDeItems) { item -> DrawerItem(...) } }
            DrawerItem("Inicio", homeIcon, onItemSelected)
            DrawerItem("Productos", productsIcon, onItemSelected)
            DrawerItem("Tienda", storeIcon, onItemSelected)
            DrawerItem("Empleados", workersIcon, onItemSelected)
            DrawerItem("Horarios", scheduleIcon, onItemSelected)
            DrawerItem("Pronosticos", forecastIcon, onItemSelected)
            DrawerItem("Caja", balanceIcon, onItemSelected)
            DrawerItem("Proveedores", providerIcon, onItemSelected)
            DrawerItem("Codigo de barras", barcodeIcon , onItemSelected)

            Spacer(Modifier.weight(1f)) // Empuja los items siguientes al fondo si es necesario

            // Items inferiores (ej: Ajustes, Cerrar Sesión)
            HorizontalDivider() // Opcional: otro divisor antes de ajustes
            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem("Ajustes", settingsIcon, onItemSelected)
            // Puedes añadir un item para "Cerrar Sesión" aquí si lo necesitas
        }
    }
}

// Este composable define CADA item individual en el Drawer
@OptIn(ExperimentalMaterial3Api::class) // Necesario para ListItem de M3
@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: (String) -> Unit) {
    // ListItem es el componente estándar de M3 para items de lista/drawer
    ListItem(
        headlineContent = { Text(label) }, // Texto principal del item
        leadingContent = { // Contenido al inicio (izquierda)
            Icon(
                imageVector = icon,
                contentDescription = null // Descripción para accesibilidad (opcional si el texto es claro)
            )
        },
        modifier = Modifier
            .clickable { onClick(label) } // Hace todo el item clickeable
            .fillMaxWidth() // Ocupa todo el ancho
            .padding(vertical = 4.dp) // Padding vertical ligero
    )
}
// --- Tu DrawerContent y DrawerItem (sin cambios, asegúrate de usar M3 si es posible) ---
// ... (código de DrawerContent y DrawerItem como en la respuesta anterior) ...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val loginViewModel = DependencyProvider.provideLoginViewModel()
    val registerViewModel = DependencyProvider.provideRegisterViewModel()
    val productViewModel = DependencyProvider.provideProductViewModel()
    val storeViewModel = DependencyProvider.provideStoreViewModel()
    val providerViewModel = DependencyProvider.provideProviderViewModel()
    val scheduleViewModel = DependencyProvider.provideScheduleViewModel()

    // Estado para el Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado para guardar el título actual (opcional pero útil)
    // Usamos rememberSaveable para que sobreviva a recomposiciones y cambios de configuración
    var currentTitle by rememberSaveable { mutableStateOf("Inicio") } // Título inicial

    // Escuchar cambios de navegación para actualizar el título
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentTitle = determineTitle(backStackEntry?.destination?.route)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // Opcional: deshabilita el gesto swipe si está cerrado
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onItemSelected = { screen ->
                        val route = when (screen) {
                            "Inicio" -> "home"
                            "Productos" -> "products"
                            "Tienda" -> "store"
                            "Empleados" -> "workers"
                            "Horarios" -> "schedule"
                            "Pronosticos" -> "forecast"
                            "Caja" -> "balance"
                            "Proveedores" -> "providers"
                            "Codigo de barras" -> "barcode"
                            "Ajustes" -> "settings"
                            else -> null
                        }
                        // Cerrar drawer antes de navegar
                        scope.launch {
                            drawerState.close()
                            route?.let {
                                // Evita navegar a la misma pantalla o crear múltiples instancias
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
        }
    ) {
        // El Scaffold ahora envuelve al NavHost
        Scaffold(
            topBar = {
                // Mostrar TopAppBar solo si NO estamos en login/register
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
                // Si no, puedes poner una TopAppBar diferente o nada
            }
        ) { innerPadding -> // Este es el padding importante
            // NavHost va DENTRO del content del Scaffold
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding) // Aplicar padding aquí
            ) {
                // --- Tus destinos (composable) ---

                // Pantallas sin TopAppBar/Drawer (si así lo decides con shouldShowTopBar)
                composable("login") { LoginScreen(navController = navController, loginViewModel) }
                composable("register") { RegisterScreen(navController = navController, registerViewModel) }

                // Pantallas con TopAppBar/Drawer
                composable("home") { HomeScreen(navController = navController) } // Ya no necesita pasar padding explícito si no lo usa directamente
                composable("testAPI") { GetAllUsers() }

                composable("products") { CategoriesScreen(navController = navController, productViewModel) }
                composable(
                    route = "products/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                    ProductsByCategoryScreen(navController, productViewModel, categoryId)
                }
                composable("store") { StoreScreen(navController = navController, storeViewModel) }
                composable("workers") { WorkersScreen(navController = navController) }
                composable("schedule") { ScheduleScreen(navController = navController, scheduleViewModel) }
                composable("forecast") { ForecastScreen(navController = navController) }
                composable("balance") { BalanceScreen(navController = navController) }
                // Ejemplo de cómo pasar el padding si la pantalla lo necesita
                composable("providers") { ProvidersScreen(navController = navController, viewModel = providerViewModel /*, paddingValues = innerPadding */) }
                composable("barcode") { BarcodeScreen(navController = navController) }
                composable("settings") { SettingsScreen(navController = navController) }
            }
        }
    }
}

// Función auxiliar para determinar el título basado en la ruta
// ¡Personalízala con tus títulos!
fun determineTitle(route: String?): String {
    return when (route) {
        "home" -> "Inicio"
        "products" -> "Categorías"
        "products/{categoryId}" -> "Productos" // Podrías querer un título más específico aquí
        "store" -> "Tienda"
        "workers" -> "Empleados"
        "schedule" -> "Horarios"
        "forecast" -> "Pronósticos"
        "balance" -> "Caja"
        "providers" -> "Proveedores"
        "barcode" -> "Código de Barras"
        "settings" -> "Ajustes"
        "testAPI" -> "Test API"
        // Rutas sin título en la TopAppBar (o un título por defecto)
        "login" -> "" // O "Login" si quieres mostrar algo
        "register" -> "" // O "Registro"
        else -> "TuKiosco" // Título por defecto
    }
}

// Función auxiliar para decidir si mostrar la TopAppBar estándar (con menú)
fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}