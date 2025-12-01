package com.example.proyectodegrado.ui.screens.whatsapp_sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.ShoppingCart
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.RefreshableContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsappSalesScreen(
    navController: NavController,
    viewModel: WhatsappSalesViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val storeId = prefs.getStoreId()?.toIntOrNull() ?: 0
    val userId = prefs.getUserId()?.toIntOrNull() ?: 0
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf<ShoppingCart?>(null) }
    var showFinalizeDialog by remember { mutableStateOf<ShoppingCart?>(null) }

    LaunchedEffect(Unit) {
        if (storeId != 0) {
            viewModel.loadCarts(storeId)
        }
    }

    LaunchedEffect(state.errorMessage, state.successMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        RefreshableContainer(
            refreshing = state.isLoading,
            onRefresh = { viewModel.loadCarts(storeId) },
            modifier = Modifier
//                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.carts.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay pedidos de WhatsApp pendientes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.carts, key = { it.id }) { cart ->
                        WhatsappOrderItem(
                            cart = cart,
                            onIncreaseQty = { pid, qty ->
                                viewModel.updateItemQuantity(cart.id, pid, qty + 1)
                            },
                            onDecreaseQty = { pid, qty ->
                                if (qty > 0) viewModel.updateItemQuantity(cart.id, pid, qty - 1)
                            },
                            onFinalizeClick = {
                                println("DEBUG: Click en botón verde de la tarjeta ${cart.id}")
                                showFinalizeDialog = cart
                                              },
                            onDeleteClick = { showDeleteDialog = cart }
                        )
                    }
                }
            }
        }
    }

    if (showFinalizeDialog != null) {
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = null },
            title = { Text("Confirmar Venta") },
            text = {
                Text("¿Deseas concretar la venta para ${showFinalizeDialog?.customerName ?: "el cliente"}?\nTotal: Bs. ${showFinalizeDialog?.totalEstimated}")
            },
            confirmButton = {
                Button(onClick = {
                    println("DEBUG: Click en COBRAR del diálogo. Enviando venta...")
                    showFinalizeDialog?.let { cart ->
                        println("DEBUG: Datos -> Cart: ${cart.id}, User: $userId, Store: $storeId")
                        viewModel.finalizeSale(cart.id, userId, storeId)
                    }
                    showFinalizeDialog = null
                }) {
                    Text("Cobrar e Imprimir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalizeDialog = null }) { Text("Cancelar") }
            }
        )
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Rechazar Pedido") },
            text = { Text("¿Estás seguro de eliminar este pedido? Esta acción es irreversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog?.let { cart ->
                            viewModel.deleteCart(cart.id, storeId)
                        }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar") }
            }
        )
    }
}