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
import com.example.proyectodegrado.ui.components.RefreshableContainer
import kotlinx.coroutines.launch

@Composable
fun CategoriesScreen(
    navController: NavController,
    viewModel: CategoryViewModel
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val uploadState by viewModel.imageUploadUiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    var showCreate by rememberSaveable { mutableStateOf(false) }
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    var categoryToInteract by remember { mutableStateOf<Category?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showSnackbar(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message) }
    }

    LaunchedEffect(Unit) { viewModel.fetchCategories(onError = ::showSnackbar) }

    fun refresh() = viewModel.fetchCategories(onError = ::showSnackbar)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm()
                    showCreate = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        RefreshableContainer(
            refreshing = loading,
            onRefresh = { refresh() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (categories.isEmpty() && !loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay categorías. ¡Agrega una!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories, key = { it.id }) { cat ->
                        CategoryItem(
                            category = cat,
                            navController = navController,
                            onEdit = {
                                viewModel.prepareFormForEdit(it)
                                categoryToInteract = it
                                showEdit = true
                            },
                            onDelete = {
                                categoryToInteract = it
                                showDelete = true
                            }
                        )
                    }
                }
            }
        }
    }

    // ---------- Crear ----------
    if (showCreate) {
        CreateCategoryDialog(
            show = true,
            onDismiss = { showCreate = false },
            formState = formState,
            imageUploadState = uploadState,
            onNameChange = viewModel::onNameChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onImageSelected = viewModel::onImageSelected,
            onCreateClick = {
                viewModel.createCategory(
                    onSuccess = { showCreate = false; refresh() },
                    onError = { showSnackbar(it) }
                )
            }
        )
    }

    // ---------- Editar ----------
    if (showEdit && categoryToInteract != null) {
        EditCategoryDialog(
            show = true,
            onDismiss = { showEdit = false },
            formState = formState,
            imageUploadState = uploadState,
            onNameChange = viewModel::onNameChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onImageSelected = viewModel::onImageSelected,
            onEdit = {
                viewModel.updateCategory(
                    id = categoryToInteract!!.id,
                    onSuccess = { showEdit = false; refresh() },
                    onError = { showSnackbar(it) }
                )
            }
        )
    }

    // ---------- Eliminar ----------
    if (showDelete && categoryToInteract != null) {
        DeleteCategoryDialog(
            show = true,
            onDismiss = { showDelete = false },
            category = categoryToInteract!!,
            onDelete = {
                viewModel.deleteCategory(
                    id = categoryToInteract!!.id,
                    onSuccess = { showDelete = false; refresh() },
                    onError = { showSnackbar(it) }
                )
            }
        )
    }
}
