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
import androidx.compose.ui.unit.sp

@Composable
fun ProductsByCategoryScreen(navController: NavController, viewModel: ProductViewModel, categoryId: Int) {
    //State variables
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var currentCategory = categoryId


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

    //Edit and delete variables
    var productToEdit by remember { mutableStateOf<Product?>(null) }
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
                onCreate = {name, description, image, SKU, brand, category_id ->
                    viewModel.createProduct(
                        request = ProductRequest(
                            SKU = SKU,
                            name = name,
                            description= description,
                            image = image,
                            brand = brand,
                            category_id = category_id
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
                category_id = currentCategory
            )
            EditProductDialog(
                show = showEditDialog,
                onDismiss = { showEditDialog = false },
                onEdit = {id, name, description, image, SKU, brand, category_id ->
                    if (productToEdit != null) {
                        viewModel.updateProduct(
                            id = id,
                            request = ProductRequest(
                                SKU = SKU,
                                name = name,
                                description = description,
                                image = image,
                                brand = brand,
                                category_id = category_id
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
                product = productToEdit
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
                                productToEdit = it
                                showEditDialog = true
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

@Composable
fun ProductItem(
    product: Product,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Brand badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colors.primary.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = product.brand,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }

            // Content section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ID: ${product.id}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Enhanced description section
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Add a "Read more" option if description is long
                if (product.description.length > 100) {
                    Text(
                        text = "Read more",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable { /* Add action here */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SKU: ${product.SKU}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Category: ${product.category_id}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onEdit(product) },
                        modifier = Modifier.padding(end = 8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    Button(
                        onClick = { onDelete(product) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    //(name, description, image, SKU, brand, category_id
    onCreate: (String, String, String, String, String, Int) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    image: String,
    onImageChange:(String) -> Unit,
    sku: String,
    onSkuChange: (String) -> Unit,
    brand: String,
    onBrandChange: (String) -> Unit,
    category_id: Int
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Producto", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre del producto") })
                    OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text("Descripción del producto") })
                    OutlinedTextField(value = sku, onValueChange = onSkuChange, label = { Text("SKU del producto") })
                    OutlinedTextField(value = brand, onValueChange = onBrandChange, label = { Text("Marca del producto") })
                    uploadImage(buttonText = "Elegir foto del producto")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, description,image,sku, brand, category_id); onDismiss() }) { Text("Crear") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    //(id, name, description, image, SKU, brand, category_id
    onEdit: (Int, String, String, String, String, String, Int) -> Unit,
    product: Product?
) {
    if (show && product != null) {
        var editedName by remember { mutableStateOf(product.name) }
        var editedDescription by remember { mutableStateOf(product.description) }
        var editedImage by remember { mutableStateOf(product.image) }
        var editedSku by remember { mutableStateOf(product.SKU) }
        var editedBrand by remember { mutableStateOf(product.brand) }
        var editedCategory by remember { mutableStateOf(product.category_id) }

        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Producto", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre del producto") })
                    OutlinedTextField(value = editedDescription, onValueChange = { editedDescription = it }, label = { Text("Descripción del producto") })
                    OutlinedTextField(value = editedSku, onValueChange = { editedSku = it }, label = { Text("SKU del producto") })
                    OutlinedTextField(value = editedBrand, onValueChange = { editedBrand = it }, label = { Text("Marca del producto") })
                    uploadImage(buttonText = "Cambiar foto del producto")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onEdit(product.id, editedName, editedDescription,editedImage, editedSku, editedBrand, editedCategory); onDismiss() }) {Text("Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteProductDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    product: Product?
) {
    if (show && product != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar el producto: ${product.name}?", style = MaterialTheme.typography.h6)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onDelete(); onDismiss() }) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}

