package com.example.proyectodegrado.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoryViewModel
) {
    val categories   by viewModel.categories.collectAsStateWithLifecycle()
    val formState    by viewModel.createCategoryFormState.collectAsStateWithLifecycle()
    val uploadState  by viewModel.imageUploadUiState.collectAsState()
    val editKey      by viewModel.editImageKey.collectAsStateWithLifecycle()

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showCreate   by rememberSaveable { mutableStateOf(false) }
    var showEdit     by rememberSaveable { mutableStateOf(false) }
    var showDelete   by rememberSaveable { mutableStateOf(false) }
    var toEdit       by remember { mutableStateOf<Category?>(null) }
    var toDelete     by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchCategories() }

    fun refresh() = viewModel.fetchCategories()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            if (categories.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay categorías")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),               // 👈 espacio externo
                    verticalArrangement = Arrangement.spacedBy(12.dp)    // 👈 espacio entre cards
                ) {
                    items(categories, key = { it.id }) { cat ->
                        CategoryItem(
                            category = cat,
                            navController = navController,
                            onEdit = { toEdit = it; showEdit = true },
                            onDelete = { toDelete = it; showDelete = true }
                        )
                    }
                }
            }
        }
    }

    // ---------- Crear ----------
    CreateCategoryDialog(
        show = showCreate,
        onDismiss = {
            showCreate = false
            viewModel.resetCreateCategoryForm()
        },
        formState = formState,
        imageUploadState = uploadState,
        onFormStateChange = viewModel::updateCreateCategoryFormState,
        onImageUriSelected = viewModel::handleCategoryImageSelection,
        onCreateClick = {
            viewModel.createCategoryFromState(
                onSuccess = { showCreate = false; refresh() },
                onError = { errorMessage = it }
            )
        }
    )

    // ---------- Editar ----------
    if (showEdit && toEdit != null) {
        EditCategoryDialog(
            show = true,
            onDismiss = { showEdit = false },
            category = toEdit!!,
            imageUploadState = uploadState,
            pendingImageKey = editKey,
            onPickNewImage = { uri -> viewModel.selectImageForEdit(toEdit!!.id, uri) },
            onClearPendingImage = { viewModel.clearEditImageKey() },
            onEdit = { req: CategoryRequest ->
                viewModel.updateCategory(
                    id = toEdit!!.id,
                    request = req,
                    onSuccess = { showEdit = false; refresh() },
                    onError = { errorMessage = it }
                )
            }
        )
    }

    // ---------- Eliminar ----------
    if (showDelete && toDelete != null) {
        DeleteCategoryDialog(
            show = true,
            onDismiss = { showDelete = false },
            category = toDelete!!,
            onDelete = {
                viewModel.deleteCategory(
                    id = toDelete!!.id,
                    onSuccess = { showDelete = false; refresh() },
                    onError = { errorMessage = it }
                )
            }
        )
    }

    // (Opcional) Mostrar errorMessage con Snackbar/AlertDialog si quieres.
}
