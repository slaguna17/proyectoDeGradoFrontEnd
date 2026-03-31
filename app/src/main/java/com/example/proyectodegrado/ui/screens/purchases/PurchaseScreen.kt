package com.example.proyectodegrado.ui.screens.purchases

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.CartItem
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.di.DependencyProvider

private fun resolveProductAvailableStock(product: Product): Int {
    return product.stock
        ?: product.stores?.firstOrNull()?.pivot?.stock
        ?: 0
}

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
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                if (state.cartItems.isEmpty()) {
                    EmptyPurchaseCartSection()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.cartItems, key = { it.productId }) { item ->
                            PurchaseCartItemRow(
                                item = item,
                                onDecrease = {
                                    viewModel.decreaseCartItemQuantity(item.productId)
                                },
                                onIncrease = {
                                    viewModel.increaseCartItemQuantity(item.productId)
                                },
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateCartItemQuantity(
                                        item.productId,
                                        newQuantity
                                    )
                                },
                                onRemove = {
                                    viewModel.removeCartItem(item.productId)
                                }
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            PurchaseProductSelectionSection(
                products = state.filteredProducts,
                searchQuery = state.searchQuery,
                draftQuantities = state.draftQuantities,
                onQueryChange = viewModel::onSearchQueryChanged,
                onDraftQuantityChange = viewModel::onDraftQuantityChanged,
                onDecreaseDraftQuantity = viewModel::decreaseDraftQuantity,
                onIncreaseDraftQuantity = viewModel::increaseDraftQuantity,
                onAddProduct = { product ->
                    viewModel.addProductToCartWithDraftQuantity(product)
                }
            )

            PurchaseCheckoutSection(
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
fun EmptyPurchaseCartSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Agrega productos para iniciar las compras",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun PurchaseProductSelectionSection(
    products: List<Product>,
    searchQuery: String,
    draftQuantities: Map<Int, String>,
    onQueryChange: (String) -> Unit,
    onDraftQuantityChange: (Int, String) -> Unit,
    onDecreaseDraftQuantity: (Int) -> Unit,
    onIncreaseDraftQuantity: (Int) -> Unit,
    onAddProduct: (Product) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            label = { Text("Buscar producto por nombre o SKU") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products, key = { it.id }) { product ->
                PurchaseProductRowWithQuantity(
                    product = product,
                    quantityText = draftQuantities[product.id].orEmpty(),
                    onQuantityChange = { newValue ->
                        onDraftQuantityChange(product.id, newValue)
                    },
                    onDecreaseClick = {
                        onDecreaseDraftQuantity(product.id)
                    },
                    onIncreaseClick = {
                        onIncreaseDraftQuantity(product.id)
                    },
                    onAddClick = {
                        onAddProduct(product)
                    }
                )
            }
        }
    }
}

@Composable
fun PurchaseProductRowWithQuantity(
    product: Product,
    quantityText: String,
    onQuantityChange: (String) -> Unit,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val availableStock = resolveProductAvailableStock(product)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PurchaseProductThumbnail(
                    imageUrl = product.imageUrl ?: product.image,
                    productName = product.name
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Costo compra: %.2f BOB".format(product.purchasePrice),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Stock actual: $availableStock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecreaseClick,
                    enabled = quantityText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Disminuir cantidad"
                    )
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = onQuantityChange,
                    label = { Text("Cant.") },
                    singleLine = true,
                    modifier = Modifier.width(92.dp),
                    keyboardOptions = FoundationKeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                IconButton(onClick = onIncreaseClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar cantidad"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}

@Composable
fun PurchaseProductThumbnail(
    imageUrl: String?,
    productName: String
) {
    val shape = RoundedCornerShape(12.dp)

    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Imagen de $productName",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    } else {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Sin imagen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PurchaseCartItemRow(
    item: CartItem,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Quitar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Costo unitario: %.2f BOB".format(item.unitPrice),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrease) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Disminuir cantidad"
                    )
                }

                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onIncrease) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aumentar cantidad"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Subtotal: %.2f BOB".format(item.unitPrice * item.quantity),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PurchaseCheckoutSection(
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
            text = "TOTAL: %.2f BOB".format(total),
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
                CircularProgressIndicator(modifier = Modifier.size(22.dp))
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