package com.example.proyectodegrado.ui.screens.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.CartItem
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.di.DependencyProvider

@Composable
fun SalesScreen(
    navController: NavController,
    viewModel: SalesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DependencyProvider.provideSalesViewModel() as T
        }
    })
) {
    val state by viewModel.uiState.collectAsState()
    val total = state.cartItems.sumOf { it.unitPrice * it.quantity }

    if (state.saleSuccess) {
        SaleSuccessDialog(
            onDismiss = { viewModel.clearCart() }
        )
    }

    Column(Modifier.fillMaxSize()) {
        // Sección del Carrito y Total
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (state.cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Agrega productos para iniciar la venta")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.cartItems, key = { it.productId }) { item ->
                        CartItemRow(
                            item = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateCartItemQuantity(item.productId, newQuantity)
                            },
                            onRemove = { viewModel.removeCartItem(item.productId) }
                        )
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Sección de productos
        ProductSelectionSection(
            products = state.filteredProducts,
            searchQuery = state.searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            onProductClick = { viewModel.addProductToCart(it) }
        )

        // Sección de Pago
        CheckoutSection(
            total = total,
            notes = state.notes,
            onNotesChange = viewModel::onNotesChanged,
            onFinalizeSale = { viewModel.registerSale() },
            isRegistering = state.isRegistering
        )
    }
}

@Composable
fun ProductSelectionSection(
    products: List<Product>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp) // Altura fija para la lista de productos
        .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            label = { Text("Buscar producto por nombre o SKU") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(products, key = { it.id }) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("Precio: %.2f BOB".format(product.price)) },
                    trailingContent = {
                        IconButton(onClick = { onProductClick(product) }) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = "Agregar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.name, modifier = Modifier.weight(1f))
            Text("x${item.quantity}")
            Spacer(Modifier.width(8.dp))
            Text("%.2f".format(item.unitPrice * item.quantity))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Quitar")
            }
        }
    }
}

@Composable
fun CheckoutSection(
    total: Double,
    notes: String,
    onNotesChange: (String) -> Unit,
    onFinalizeSale: () -> Unit,
    isRegistering: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "TOTAL: %.2f BOB".format(total),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End)
        )
        Button(
            onClick = onFinalizeSale,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Finalizar Venta")
            }
        }
    }
}

@Composable
fun SaleSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éxito") },
        text = { Text("La venta se ha registrado correctamente.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Nueva Venta")
            }
        }
    )
}