package com.example.proyectodegrado.ui.screens.products

import CategoryItem
import android.net.Uri // Necesario
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Importa Material 3
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // Para botones de icono
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold // Si es necesario
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog // Para errores
import androidx.compose.material3.TextButton
// Importa Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
// Importa Runtime y Lifecycle
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
// Otras importaciones
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.CreateCategoryFormState
// Importa el estado del ViewModel y el diálogo
import com.example.proyectodegrado.di.DependencyProvider // O como obtengas tu ViewModel
import com.example.proyectodegrado.ui.components.UploadImageState

@Composable
fun CategoriesScreen(
    navController: NavController,
    // Obtén el ViewModel correctamente (ejemplo usando el provider)
    viewModel: ProductViewModel = remember { DependencyProvider.provideProductViewModel() }
) {
    // --- Estados del ViewModel ---
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categoryFormState by viewModel.createCategoryFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    // --- Estados de la UI ---
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) } // Para errores generales o de creación/borrado
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Estados para Editar/Eliminar (mantener por ahora)
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // --- Lógica de Refresco ---
    val refreshCategories: () -> Unit = {
        viewModel.fetchCategories(
            onSuccess = { errorMessage = null }, // La lista se actualiza sola vía StateFlow
            onError = { errorMsg -> errorMessage = "Error al cargar categorías: $errorMsg" }
        )
    }

    // --- Efecto para Carga Inicial ---
    LaunchedEffect(key1 = Unit) {
        refreshCategories()
    }

    // --- UI Principal ---
    // Asumiendo que esta pantalla está dentro del Scaffold de Navigation.kt
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Padding interno
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // --- Botón Crear Categoría ---
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.updateCreateCategoryFormState(CreateCategoryFormState()) // Limpia el formulario en ViewModel
                errorMessage = null // Limpia errores
                showCreateDialog = true
            }
        ) {
            Text("Crear Nueva Categoría")
        }

        // --- Lista de Categorías ---
        if (categories.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f) // Ocupa espacio restante
            ) {
                items(categories) { category ->
                    CategoryItem( // Usa tu Composable CategoryItem
                        category = category,
                        navController = navController,
                        onEdit = {
                            categoryToEdit = it
                            // TODO: Necesitarás un estado en ViewModel para el formulario de edición de categoría
                            // viewModel.startEditingCategory(it)
                            showEditDialog = true
                        },
                        onDelete = {
                            categoryToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        } else {
            // Muestra mensaje si no hay categorías (y no hay error)
            if (errorMessage == null) {
                Spacer(Modifier.height(16.dp))
                Text("No hay categorías creadas.", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            // Podrías mostrar un CircularProgressIndicator si quieres indicar carga inicial
            // else { CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally)) }
        }

        // --- Indicador de Error General ---
        errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { errorMessage = null },
                title = { Text("Error") },
                text = { Text(msg) },
                confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("OK") } }
            )
        }

    } // Fin Column principal

    // --- Diálogos ---
    if (showCreateDialog) {
        CreateCategoryDialog(
            show = true,
            onDismiss = { showCreateDialog = false },
            formState = categoryFormState, // <-- Pasa estado del ViewModel
            imageUploadState = imageUploadState, // <-- Pasa estado de subida del ViewModel
            onFormStateChange = { newState -> viewModel.updateCreateCategoryFormState(newState) }, // <-- Llama a ViewModel
            onImageUriSelected = { uri -> viewModel.handleCategoryImageSelection(uri) }, // <-- Llama a ViewModel
            onCreateClick = {
                viewModel.createCategoryFromState(
                    onSuccess = {
                        showCreateDialog = false // Cierra en éxito
                        errorMessage = null
                        // La lista se refresca automáticamente en el ViewModel
                    },
                    onError = { errorMsg ->
                        errorMessage = errorMsg // Muestra el error en el AlertDialog general
                        // No cierres el diálogo
                    }
                )
            }
        )
    } // Fin showCreateDialog

    if (showEditDialog) {
        // TODO: Implementar EditCategoryDialog usando un patrón similar
        // Necesitará su propio estado de formulario en el ViewModel y manejar la subida de imagen si se cambia.
        EditCategoryDialog(
            show = showEditDialog,
            onDismiss = { showEditDialog = false },
            onEdit = { id, name, description, image ->
                if (categoryToEdit != null) {
                    // Idealmente, llama a una función en ViewModel que maneje la subida (si la imagen cambió)
                    // y luego llame a categoryRepository.updateCategory
                    viewModel.updateCategory(
                        id = id,
                        request = CategoryRequest(name, description, image), // Necesita la URL actualizada si cambió
                        onSuccess = { showEditDialog = false },
                        onError = { errorMsg -> errorMessage = errorMsg; showEditDialog = false }
                    )
                }
            },
            category = categoryToEdit
        )
    }

    if (showDeleteDialog) {
        DeleteCategoryDialog( // Asume que DeleteCategoryDialog existe
            show = true,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                categoryToDelete?.let { category ->
                    viewModel.deleteCategory(
                        id = category.id,
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
            category = categoryToDelete
        )
    } // Fin showDeleteDialog
}

