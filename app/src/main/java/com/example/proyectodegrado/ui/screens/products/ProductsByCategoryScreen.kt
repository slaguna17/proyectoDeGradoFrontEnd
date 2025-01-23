package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.R
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.ui.components.Header


@Composable
fun ProductsByCategoryScreen(navController: NavController, viewModel: ProductViewModel, categoryId: Int) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        viewModel.fetchProductsByCategory(
            onSuccess = {
                products = viewModel.productsByCategory.value
            },
            onError = { errorMessage = it },
            categoryId = categoryId
        )
    }

    Scaffold(
        topBar = { Header(navController = navController) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (products.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        ProductItem(product = product)
                    }
                }
            } else {
                Text("No hay productos en esta categoría.")
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ){
            Column(
                Modifier.weight(1f)
            ) {
                Text(text = "ID: ${product.id}")
                Text(text = "Name: ${product.name}")
                Text(text = "Description: ${product.description}")
                Text(text = "SKU: ${product.SKU}")
                Text(text = "Image: ${product.image}")
                Text(text = "Brand: ${product.brand}")
                Text(text = "Category ID: ${product.category_id}")
            }
            Row {
                IconButton(onClick = { /* Implement edit logic */ }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}
