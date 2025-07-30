package com.example.proyectodegrado.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CreateCategoryFormState
import com.example.proyectodegrado.ui.components.RefreshableContainer

@OptIn(ExperimentalMaterial3Api::class)
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

    var toEdit       by remember { mutableStateOf<Category?>(null) }
    var toDelete     by remember { mutableStateOf<Category?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingFirstTime by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun refreshCategories() {
        isRefreshing = true
        viewModel.fetchCategories(
            onSuccess = {
                isRefreshing = false
                isLoadingFirstTime = false
            },
            onError = {
                errorMessage = it
                isRefreshing = false
                isLoadingFirstTime = false
            }
        )
    }

    LaunchedEffect(Unit) {
        if (categories.isEmpty()) {
            refreshCategories()
        } else {
            isLoadingFirstTime = false
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.updateCreateCategoryFormState(CreateCategoryFormState())
                showCreate = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Categoría")
            }
        }
    ) { innerPadding ->
        RefreshableContainer(
            refreshing = isRefreshing,
            onRefresh = { refreshCategories() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (isLoadingFirstTime) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (categories.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay categorías.", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { cat ->
                        CategoryItem(
                            category = cat,
                            navController = navController,
                            onEdit = {
                                toEdit = it
                                showEdit = true
                            },
                            onDelete = {
                                toDelete = it
                                showDelete = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreate) {
        CreateCategoryDialog(
            show = true,
            onDismiss = { showCreate = false },
            formState = formState,
            imageUploadState = imageState,
            onFormStateChange = viewModel::updateCreateCategoryFormState,
            onImageUriSelected = viewModel::handleCategoryImageSelection,
            onCreateClick = {
                viewModel.createCategoryFromState(
                    onSuccess = {
                        showCreate = false
                        refreshCategories()
                    },
                    onError = { errorMessage = it }
                )
            }
        )
    }

    if (showEdit && toEdit != null) {
        EditCategoryDialog(
            show = true,
            onDismiss = { showEdit = false },
            category = toEdit!!,
            onEdit = { req ->
                viewModel.updateCategory(
                    id = toEdit!!.id,
                    request = req,
                    onSuccess = {
                        showEdit = false
                        refreshCategories()
                    },
                    onError = { errorMessage = it }
                )
            }
        )
    }

    if (showDelete && toDelete != null) {
        DeleteCategoryDialog(
            show = true,
            onDismiss = { showDelete = false },
            category = toDelete!!,
            onDelete = {
                viewModel.deleteCategory(
                    id = toDelete!!.id,
                    onSuccess = {
                        showDelete = false
                        refreshCategories()
                    },
                    onError = { errorMessage = it }
                )
            }
        )
    }
}
