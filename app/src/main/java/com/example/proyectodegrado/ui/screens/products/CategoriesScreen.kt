package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.ui.components.Header
import com.example.proyectodegrado.ui.components.uploadImage

@Composable
fun CategoriesScreen(navController: NavController, viewModel: ProductViewModel){
    //State variables
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    //Dialog variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Create Category variables
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryDescription by remember { mutableStateOf("") }
    var newCategoryImage by remember { mutableStateOf("") }

    //Edit and delete variables
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    // Refresh function
    val refreshCategories: () -> Unit = {
        viewModel.fetchCategories(
            onSuccess = { categories = viewModel.categories.value },
            onError = { errorMessage = it }
        )
    }
    // Load categories when initializing screeen
    LaunchedEffect(Unit) {
        refreshCategories()
    }
    Scaffold(
        topBar = { Header(navController = navController, title = "Categorias")},
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Dialogs
                CreateCategoryDialog(
                    show = showCreateDialog,
                    onDismiss = { showCreateDialog = false },
                    onCreate = {name, description, image ->
                        viewModel.createCategory(
                            request = CategoryRequest(name, description, image),
                            onSuccess = {
                                refreshCategories() // Refresh after creating
                                newCategoryName = ""
                                newCategoryDescription = ""
                                newCategoryImage = ""
                            },
                            onError = {
                                errorMessage = it
                            }
                        )
                    },
                    name = newCategoryName,
                    onNameChange = {newCategoryName = it},
                    description = newCategoryDescription,
                    onDescriptionChange = {newCategoryDescription = it},
                    image = newCategoryImage,
                    onImageChange = {newCategoryImage = it}
                )
                EditCategoryDialog(
                    show = showEditDialog,
                    onDismiss = { showEditDialog = false },
                    onEdit = {id, name, description, image ->
                        if (categoryToEdit != null) {
                            viewModel.updateCategory(
                                id = id,
                                request = CategoryRequest(name, description, image),
                                onSuccess = {
                                    refreshCategories()
                                },
                                onError = { errorMessage = it }
                            )

                        }
                    },
                    category = categoryToEdit
                )
                DeleteCategoryDialog(
                    show = showDeleteDialog,
                    onDismiss = { showDeleteDialog = false },
                    onDelete = {
                        if (categoryToDelete != null) {
                            viewModel.deleteCategory(
                                id = categoryToDelete!!.id,
                                onSuccess = {
                                    refreshCategories()
                                },
                                onError = {
                                    errorMessage = it
                                }
                            )
                        }
                    },
                    category = categoryToDelete
                )
                //Create Category
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                    showCreateDialog = true
                }) {
                    Text("Crear Categoria")
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (categories.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(categories) { category ->
                            CategoryItem(
                                category = category,
                                navController= navController,
                                onEdit = {
                                    categoryToEdit = it
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
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
            }

        }
    )
}