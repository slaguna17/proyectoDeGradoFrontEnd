// app/src/main/java/com/example/proyectodegrado/ui/screens/products/AllProductsScreen.kt
package com.example.proyectodegrado.ui.screens.products

import android.net.Uri // Necesario para el callback onImageUriSelected
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// Importa Material 3 si lo usas consistentemente
import androidx.compose.material3.Button // Ejemplo M3
import androidx.compose.material3.CircularProgressIndicator // Ejemplo M3
import androidx.compose.material3.Scaffold // Ejemplo M3 (O usa el Scaffold de Navigation)
import androidx.compose.material3.Text // Ejemplo M3
import androidx.compose.material3.AlertDialogDefaults.containerColor // Ejemplo M3
import androidx.compose.material3.AlertDialog // Ejemplo M3
import androidx.compose.material3.TextButton // Ejemplo M3
import androidx.compose.material3.MaterialTheme // Ejemplo M3
import androidx.compose.runtime.* // Importa collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Import específico
import androidx.compose.runtime.saveable.rememberSaveable // Para guardar estado simple
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CreateProductFormState
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest2 // Lo usas para Edit, ok por ahora
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider // Importa tu DI
// Al principio de AllProductsScreen.kt y ProductViewModel.kt

@Composable
fun AllProductsScreen(
    navController: NavController,
    viewModel: ProductViewModel, // Recibe el ViewModel (asegúrate que se inyecte correctamente)
    categoryId: Int // <-- NECESARIO: Asume que el ID de la categoría se pasa a esta pantalla
) {
    // --- Estados de la UI ---
    // El estado de la lista de productos ahora se obtiene del ViewModel
    val productsByCategory by viewModel.productsByCategory.collectAsStateWithLifecycle()
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) } // Para errores generales o de creación

    // Estados para visibilidad de diálogos
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) } // Mantener por ahora para Edit/Delete
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Estados para Editar/Eliminar (mantener por ahora, se refactorizarán después)
    var productToEdit by remember { mutableStateOf<ProductRequest2?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // --- Estados del ViewModel para el diálogo de CREACIÓN ---
    val createFormState by viewModel.createProductFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    // --- Preferencias y IDs ---
    val context = LocalContext.current
    val storePreferences = remember { AppPreferences(context) }
    // Obtenemos storeId una vez, no necesita ser `remember` si no cambia durante la vida del composable
    val storeId = remember { storePreferences.getStoreId()?.toIntOrNull() ?: 0 }

    // --- Lógica de Refresco ---
    val refreshProducts: () -> Unit = {
        // Llama al ViewModel para refrescar los productos de ESTA categoría
        viewModel.fetchProductsByCategory(
            categoryId = categoryId,
            onSuccess = {
                // No necesitas hacer nada aquí, el StateFlow productsByCategory se actualizará solo
                errorMessage = null // Limpia errores previos
            },
            onError = { errorMsg -> errorMessage = "Error al cargar productos: $errorMsg" }
        )
    }

    // --- Efecto para Carga Inicial ---
    LaunchedEffect(key1 = categoryId) { // Se relanza si categoryId cambia
        if (storeId == 0) {
            errorMessage = "Error: ID de tienda no encontrado."
            // Considera navegar atrás o mostrar un error más permanente
        } else {
            refreshProducts()
        }
    }

    // --- UI Principal ---
    // Nota: Si esta pantalla ya está dentro del Scaffold de Navigation.kt,
    // NO necesitas otro Scaffold aquí. Solo usa Column con el padding.
    // Asumiendo que SÍ necesitas un Scaffold aquí por ahora:
    /*
    Scaffold { paddingValues -> // Si usas el Scaffold de Navigation, recibe paddingValues como parámetro
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica padding del Scaffold padre si existe
                .padding(16.dp), // Padding interno de la pantalla
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
           // ... contenido ...
        }
    }
    */

    // Asumiendo que NO hay Scaffold aquí y recibes paddingValues de Navigation.kt
    Column(
        modifier = Modifier
            .fillMaxSize()
            // .padding(paddingValues) // Descomenta si recibes padding de Navigation.kt
            .padding(16.dp), // Padding interno
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Botón para Crear Producto ---
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                // Resetea/Prepara el estado del formulario en el ViewModel antes de abrir
                viewModel.updateCreateProductFormState(CreateProductFormState(categoryId = categoryId))
                errorMessage = null // Limpia errores previos
                showCreateDialog = true
            },
            // Deshabilita si storeId no es válido
            enabled = storeId != 0
        ) {
            Text("Crear Producto en esta categoría")
        }
        if (storeId == 0) {
            Text("Error: No se pudo determinar la tienda.", color = MaterialTheme.colorScheme.error)
        }

        // --- Lista de Productos ---
        if (productsByCategory.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                items(productsByCategory) { product ->
                    ProductItem( // Asume que ProductItem existe
                        product = product,
                        onEdit = {
                            // TODO: Refactorizar EditProductDialog y su lógica de estado/ViewModel
                            // productToEdit = ...
                            // showEditDialog = true
                        },
                        onDelete = {
                            productToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        } else {
            // Muestra un mensaje si no hay productos (y no hay error)
            if (errorMessage == null) {
                Spacer(Modifier.height(16.dp))
                Text("No hay productos en esta categoría.", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }

        // --- Indicador de Error General ---
        errorMessage?.let { msg ->
            // Podrías usar un Snackbar o un AlertDialog como antes
            Text(msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

    } // Fin Column principal

    // --- Diálogos ---
    if (showCreateDialog) {
        CreateProductDialog(
            show = true,
            onDismiss = { showCreateDialog = false },
            formState = createFormState, // Pasa el estado del ViewModel
            imageUploadState = imageUploadState, // Pasa el estado de subida
            onFormStateChange = { newState ->
                // Llama al ViewModel para actualizar el estado del formulario
                viewModel.updateCreateProductFormState(newState)
            },
            onImageUriSelected = { uri ->
                // Llama al ViewModel para manejar la selección/subida
                viewModel.handleProductImageSelection(uri)
            },
            onCreateClick = { categoryIdFromDialog, storeIdFromDialog -> // Recibe los IDs (aunque ya los tenemos aquí)
                viewModel.createProductFromState(
                    categoryId = categoryId, // Usa el ID de la categoría actual
                    storeId = storeId,       // Usa el ID de la tienda actual
                    onSuccess = {
                        showCreateDialog = false // Cierra en éxito
                        errorMessage = null // Limpia cualquier error previo
                        // refreshProducts() // createProductFromState ya refresca
                        // Mostrar mensaje éxito (Snackbar es ideal)
                    },
                    onError = { errorMsg ->
                        errorMessage = errorMsg // Muestra el error
                        // No cierres el diálogo
                    }
                )
            }
            // Ya no se pasan: name, onNameChange, image, onImageChange, etc.
        )
    } // Fin showCreateDialog

    if (showEditDialog) {
        // TODO: Implementar EditProductDialog usando un patrón similar
        // EditProductDialog(...)
    }

    if (showDeleteDialog) {
        // El DeleteProductDialog actual puede funcionar si solo necesita el `product`
        DeleteProductDialog(
            show = true,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                productToDelete?.let { product ->
                    viewModel.deleteProduct(
                        id = product.id,
                        categoryId = categoryId, // Pasa categoryId para refrescar
                        onSuccess = {
                            showDeleteDialog = false
                            errorMessage = null
                            // refreshProducts() // deleteProduct ya refresca
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

// --- Asegúrate que tienes el Composable ProductItem definido en otro lugar ---
// @Composable
// fun ProductItem(product: Product, onEdit: (Product) -> Unit, onDelete: (Product) -> Unit) { ... }