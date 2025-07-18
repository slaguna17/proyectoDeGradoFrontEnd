package com.example.proyectodegrado.ui.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CreateCategoryFormState
import com.example.proyectodegrado.ui.components.RefreshableContainer

@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoryViewModel
) {
    val categories   by viewModel.categories.collectAsStateWithLifecycle()
    val formState    by viewModel.createCategoryFormState.collectAsStateWithLifecycle()
    val imageState   by viewModel.imageUploadUiState.collectAsStateWithLifecycle()

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showCreate   by rememberSaveable { mutableStateOf(false) }
    var showEdit     by rememberSaveable { mutableStateOf(false) }
    var showDelete   by rememberSaveable { mutableStateOf(false) }

    var toEdit   by remember { mutableStateOf<Category?>(null) }
    var toDelete by remember { mutableStateOf<Category?>(null) }

    // Para Swipe Refresh
    var isRefreshing by remember { mutableStateOf(false) }

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.fetchCategories(onError = { errorMessage = it })
    }

    // Función para refrescar (swipe)
    fun refreshCategories() {
        isRefreshing = true
        viewModel.fetchCategories(
            onSuccess = { isRefreshing = false },
            onError   = { error ->
                errorMessage = error
                isRefreshing = false
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.updateCreateCategoryFormState(CreateCategoryFormState())
            showCreate = true
        }) {
            Text("+ Nueva Categoría")
        }
        Spacer(Modifier.height(8.dp))

        // REFRESCO (Swipe down)
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshCategories() },
            modifier = Modifier.weight(1f)
        ) {
            if (categories.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Cargando categorías...", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            } else {
                LazyColumn {
                    items(categories) { cat ->
                        CategoryItem(
                            category      = cat,
                            navController = navController,
                            onEdit        = { toEdit = it; showEdit = true },
                            onDelete      = { toDelete = it; showDelete = true }
                        )
                    }
                }
            }
        }
    }

    // Diálogos
    if (showCreate) {
        CreateCategoryDialog(
            show              = true,
            onDismiss         = { showCreate = false },
            formState         = formState,
            imageUploadState  = imageState,
            onFormStateChange = viewModel::updateCreateCategoryFormState,
            onImageUriSelected= viewModel::handleCategoryImageSelection,
            onCreateClick     = {
                viewModel.createCategoryFromState(onError = { errorMessage = it })
                showCreate = false
            }
        )
    }

    if (showEdit && toEdit != null) {
        EditCategoryDialog(
            show      = true,
            onDismiss = { showEdit = false },
            category  = toEdit!!,
            onEdit    = { req ->
                viewModel.updateCategory(toEdit!!.id, req, onError = { errorMessage = it })
                showEdit = false
            }
        )
    }

    if (showDelete && toDelete != null) {
        DeleteCategoryDialog(
            show     = true,
            onDismiss= { showDelete = false },
            category = toDelete!!,
            onDelete = {
                viewModel.deleteCategory(toDelete!!.id, onError = { errorMessage = it })
                showDelete = false
            }
        )
    }
}
