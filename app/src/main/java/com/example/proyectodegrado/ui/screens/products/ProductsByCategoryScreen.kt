package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.ui.components.Header
import com.example.proyectodegrado.ui.components.uploadImage
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.proyectodegrado.data.model.ProductData
import com.example.proyectodegrado.data.model.ProductRequest2
import com.example.proyectodegrado.data.model.StoreData
import com.example.proyectodegrado.di.AppPreferences

@Composable
fun ProductsByCategoryScreen(navController: NavController, viewModel: ProductViewModel, categoryId: Int) {
    //State variables
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var currentCategory = categoryId

    //App preferences
    val context = LocalContext.current
    val storePreferences = remember { AppPreferences(context) }
    var storeId by remember { mutableIntStateOf(storePreferences.getStoreId()?.toInt() ?: 0) }
    
    //Dialog variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Create Product variables
    var newProductName by remember { mutableStateOf("") }
    var newProductDescription by remember { mutableStateOf("") }
    var newProductImage by remember { mutableStateOf("") }
    var newProductSKU by remember { mutableStateOf("") }
    var newProductBrand by remember { mutableStateOf("") }
    var newStock by remember { mutableStateOf(0) }
    var newExpirationDate by remember { mutableStateOf("") }

    //Edit and delete variables
    var productToEdit by remember { mutableStateOf<ProductRequest2?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // Refresh function
    val refreshProducts: () -> Unit = {
        viewModel.fetchProductsByCategory(
            onSuccess = { products = viewModel.productsByCategory.value },
            onError = { errorMessage = it },
            categoryId = categoryId
        )
    }
    LaunchedEffect(categoryId) {
        storeId = storePreferences.getStoreId()?.toInt() ?: 0
        refreshProducts()
    }

    Scaffold(
        topBar = { Header(navController = navController, title = "Productos") },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Dialogs
            CreateProductDialog(
                show = showCreateDialog,
                onDismiss = { showCreateDialog = false },
                onCreate = {name, description, image, SKU, brand, category_id, stock, expiration_date ->
                    viewModel.createProduct(
                        request = ProductRequest(
                            product = ProductData(
                                SKU = SKU,
                                name = name,
                                description= description,
                                image = image,
                                brand = brand,
                                category_id = category_id
                            ),
                            store = StoreData(
                                store_id = storeId,
                                stock = stock,
                                expiration_date = expiration_date
                            )
                        ),
                        onSuccess = {
                            refreshProducts()
                            newProductName = ""
                            newProductDescription = ""
                            newProductImage = ""
                            newProductSKU = ""
                            newProductBrand = ""
                        },
                        onError = {
                            errorMessage = it
                        }
                    )
                },
                name = newProductName,
                onNameChange = {newProductName = it},
                description = newProductDescription,
                onDescriptionChange = {newProductDescription = it},
                image = newProductImage,
                onImageChange = {newProductImage = it},
                sku = newProductSKU,
                onSkuChange = {newProductSKU = it},
                brand = newProductBrand,
                onBrandChange = {newProductBrand = it},
                category_id = currentCategory,
                stock = newStock,
                onStockChange = {newStock = it.toInt()},
                expiration_date = newExpirationDate,
                onDateChange = {newExpirationDate = it}
            )
            EditProductDialog(
                show = showEditDialog,
                onDismiss = { showEditDialog = false },
                onEdit = {id, name, description, image, SKU, brand, category_id, stock, expiration_date ->
                    if (productToEdit != null) {
                        viewModel.updateProduct(
                            id = id,
                            request = ProductRequest(
                                product = ProductData(
                                    SKU = SKU,
                                    name = name,
                                    description= description,
                                    image = image,
                                    brand = brand,
                                    category_id = category_id
                                ),
                                store = StoreData(
                                    store_id = storeId,
                                    stock = stock,
                                    expiration_date = expiration_date
                                )
                            ),
                            onSuccess = {
                                refreshProducts()
                            },
                            onError = {
                                errorMessage = it
                            }
                        )

                    }
                },
                productRequest = productToEdit
            )
            DeleteProductDialog(
                show = showDeleteDialog,
                onDismiss = { showDeleteDialog = false },
                onDelete = {
                    if (productToDelete != null) {
                        viewModel.deleteProduct(
                            id = productToDelete!!.id,
                            onSuccess = {
                                refreshProducts()
                            },
                            onError = {
                                errorMessage = it
                            }
                        )
                    }
                },
                product = productToDelete
            )
            Text(text = "Tienda seleccionada = ${storeId}")
            //Create Product in category
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                showCreateDialog = true
            }) {
                Text("Crear Producto en esta categoria")
            }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (products.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductItem(
                            product = product,
                            onEdit = {
//                                productToEdit = ProductRequest2(
//                                    product = it,
//                                    store = StoreData(
//                                        store_id = storeId,
//                                        stock = ,
//                                        expiration_date =
//                                    ))
//                                showEditDialog = true
                            },
                            onDelete = {
                                productToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            } else {
//                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                Text("No hay productos en esta categoría.")
            }
        }
    }
}