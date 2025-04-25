package com.example.proyectodegrado.ui.screens.products

import android.net.Uri // Necesario para el callback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// Importaciones de Material 3
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator // Si lo necesitas para carga inicial
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // Si esta pantalla tuviera su propio Scaffold
import androidx.compose.material3.FloatingActionButton // Ejemplo si usaras FAB
import androidx.compose.material3.Icon // Ejemplo si usaras FAB
import androidx.compose.material.icons.Icons // Para iconos base
import androidx.compose.material.icons.filled.Add // Ejemplo para FAB
// Importaciones de Runtime y Lifecycle
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
// Otras importaciones
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest2 // Usado para Edit
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider
// Importa tus diálogos y el estado del ViewModel
import com.example.proyectodegrado.ui.components.UploadImageState // Asegúrate de importar esto

@Composable
fun ProductsByCategoryScreen(
    navController: NavController,
    viewModel: ProductViewModel, // Recibe el ViewModel (o usa provideProductViewModel)
    categoryId: Int // Recibe el ID de la categoría
) {
    // --- Estados de la UI ---
    val productsByCategory by viewModel.productsByCategory.collectAsStateWithLifecycle() // Observa la lista filtrada
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    // Estados para visibilidad de diálogos
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Estados para Editar/Eliminar (mantener por ahora)
    var productToEdit by remember { mutableStateOf<ProductRequest2?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // --- Estados del ViewModel para el diálogo de CREACIÓN ---
    val createFormState by viewModel.createProductFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    // --- Preferencias y IDs ---
    val context = LocalContext.current
    val storePreferences = remember { AppPreferences(context) }
    val storeId = remember { storePreferences.getStoreId()?.toIntOrNull() ?: 0 }

    // --- Lógica de Refresco ---
    val refreshProducts: () -> Unit = {
        viewModel.fetchProductsByCategory(
            categoryId = categoryId,
            onSuccess = {
                errorMessage = null // Limpia errores
            },
            onError = { errorMsg -> errorMessage = "Error al cargar productos: $errorMsg" }
        )
    }

    // --- Efecto para Carga Inicial ---
    LaunchedEffect(key1 = categoryId, key2 = storeId) { // Se relanza si categoryId o storeId cambian
        if (storeId == 0) {
            errorMessage = "Error: ID de tienda no encontrado."
        } else if (categoryId > 0) { // Asegura que categoryId sea válido
            refreshProducts()
        } else {
            errorMessage = "Error: ID de categoría no válido."
        }
    }

    // --- UI Principal ---
    // Asumiendo que esta pantalla NO tiene su propio Scaffold y usa el de Navigation.kt
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Padding interno
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Botón para Crear Producto ---
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                // Prepara el estado del formulario en el ViewModel con el categoryId actual
                viewModel.updateCreateProductFormState(CreateProductFormState(categoryId = categoryId))
                errorMessage = null // Limpia errores
                showCreateDialog = true
            },
            enabled = storeId != 0 && categoryId > 0 // Habilita solo si tenemos IDs válidos
        ) {
            Text("Crear Producto en esta categoría")
        }
        // Muestra errores de inicialización si existen
        if (storeId == 0) { Text("Error: No se pudo determinar la tienda.", color = MaterialTheme.colorScheme.error) }
        if (categoryId <= 0) { Text("Error: Categoría no válida.", color = MaterialTheme.colorScheme.error) }


        // --- Lista de Productos ---
        // Muestra indicador de carga o la lista/mensaje
        if (productsByCategory.isEmpty() && errorMessage == null) {
            // Podrías mostrar un CircularProgressIndicator mientras carga la primera vez
            // o simplemente el texto de "No hay productos".
            Spacer(Modifier.height(16.dp))
            Text("No hay productos en esta categoría.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (productsByCategory.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f) // Ocupa espacio restante
            ) {
                items(productsByCategory) { product ->
                    ProductItem( // Asume que ProductItem existe
                        product = product,
                        onEdit = {
                            // TODO: Implementar lógica de edición
                        },
                        onDelete = {
                            productToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // --- Indicador de Error General ---
        errorMessage?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

    } // Fin Column principal

    // --- Diálogos ---
    if (showCreateDialog) {
        CreateProductDialog(
            show = true,
            onDismiss = { showCreateDialog = false },
            formState = createFormState, // Pasa estado del ViewModel
            imageUploadState = imageUploadState, // Pasa estado de subida del ViewModel
            onFormStateChange = { newState -> viewModel.updateCreateProductFormState(newState) }, // Llama a ViewModel
            onImageUriSelected = { uri -> viewModel.handleProductImageSelection(uri) }, // Llama a ViewModel
            onCreateClick = { categoryIdFromDialog, storeIdFromDialog -> // IDs del diálogo (ignóralos o usa los de la pantalla)
                viewModel.createProductFromState(
                    categoryId = categoryId, // Usa el ID de la categoría de la pantalla
                    storeId = storeId,       // Usa el ID de la tienda de la pantalla
                    onSuccess = {
                        showCreateDialog = false // Cierra en éxito
                        errorMessage = null
                        // La lista se refresca automáticamente porque createProductFromState llama a fetchProductsByCategory
                    },
                    onError = { errorMsg ->
                        errorMessage = errorMsg // Muestra el error
                    }
                )
            }
        )
    } // Fin showCreateDialog

    if (showEditDialog) {
        // TODO: Implementar EditProductDialog usando patrón similar
        // EditProductDialog(...)
    }

    if (showDeleteDialog) {
        DeleteProductDialog( // Asume que DeleteProductDialog existe
            show = true,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                productToDelete?.let { product ->
                    viewModel.deleteProduct(
                        id = product.id,
                        categoryId = categoryId, // Pasa categoryId para refrescar la lista correcta
                        onSuccess = {
                            showDeleteDialog = false
                            errorMessage = null
                            // La lista se refresca automáticamente
                        },
                        onError = { errorMsg ->
                            errorMessage = "Error al eliminar: $errorMsg"
                            showDeleteDialog = false
                        }
                    )
                }
            },
            product = productToDelete
        )
    } // Fin showDeleteDialog
}

// --- Composable ProductItem (Asegúrate que exista) ---
// @Composable
// fun ProductItem(product: Product, onEdit: (Product) -> Unit, onDelete: (Product) -> Unit) { ... }