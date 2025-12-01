package com.example.proyectodegrado.ui.screens.purchases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DependencyProvider.providePurchasesViewModel() as T
        }
    })
) {
    val state by viewModel.uiState.collectAsState()
    val total = state.cartItems.sumOf { it.unitPrice * it.quantity }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (state.purchaseSuccess) {
        PurchaseSuccessDialog(
            onDismiss = { viewModel.clearCart() }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                        Text("Agrega productos para iniciar las compras")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.cartItems, key = { it.productId }) { item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateCartItemQuantity(
                                        item.productId,
                                        newQuantity
                                    )
                                },
                                onRemove = { viewModel.removeCartItem(item.productId) }
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            ProductSelectionSection(
                products = state.filteredProducts,
                searchQuery = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onProductClick = { viewModel.addProductToCart(it) }
            )

            CheckoutSection(
                total = total,
                notes = state.notes,
                onNotesChange = viewModel::onNotesChanged,
                onFinalizePurchase = { viewModel.registerPurchase() },
                isRegistering = state.isRegistering
            )
        }
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
        .height(250.dp)
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
                    supportingContent = { Text("Precio: %.2f BOB".format(product.purchasePrice)) },
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
    onFinalizePurchase: () -> Unit,
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
            onClick = onFinalizePurchase,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Finalizar Compra")
            }
        }
    }
}

@Composable
fun PurchaseSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Éxito") },
        text = { Text("La compra se ha registrado correctamente.") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Nueva Compra")
            }
        }
    )
}