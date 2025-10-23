package com.example.proyectodegrado.ui.screens.products

import android.widget.GridLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Product
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: Product,
    currentStoreId: Int?,
    allStores: List<StoreOption>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onAssignToStore: (Product) -> Unit,
    onRemoveFromStore: (product: Product, storeId: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                if (!product.image.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl ?: product.image,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Sin imagen",
                        modifier = Modifier.size(64.dp).align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                }
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = product.brand,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(text = product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Bs: ${product.price}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                Text(text = "SKU: ${product.sku ?: "No definido"}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(16.dp))

                if (currentStoreId != null) {
                    val stockForCurrentStore = product.stores?.firstOrNull { it.id == currentStoreId }?.pivot?.stock
                    StockInfoSingleStore(stock = stockForCurrentStore)
                } else {
                    StockInfoAllStores(
                        product = product,
                        allStores = allStores,
                        onAssignToStore = onAssignToStore,
                        onRemoveFromStore = { storeId -> onRemoveFromStore(product, storeId) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // --- BOTONES DE ACCIÓN PRINCIPALES ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = { onEdit(product) },
                        modifier = Modifier.padding(end = 8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }
                    Button(
                        onClick = { onDelete(product) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}


@Composable
private fun StockInfoSingleStore(stock: Int?) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Stock en tienda", style = MaterialTheme.typography.labelLarge)
            Text(
                text = (stock ?: 0).toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockInfoAllStores(
    product: Product,
    allStores: List<StoreOption>,
    onAssignToStore: (Product) -> Unit,
    onRemoveFromStore: (storeId: Int) -> Unit
) {
    val assignedStoreIds = product.stores?.map { it.id }?.toSet() ?: emptySet()
    val availableStores = allStores.filterNot { it.id in assignedStoreIds }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Stock por Tienda:", style = MaterialTheme.typography.titleSmall)
        if (product.stores.isNullOrEmpty()) {
            Text("Este producto no está asignado a ninguna tienda.", style = MaterialTheme.typography.bodyMedium)
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 4.dp
            ) {
                product.stores.forEach { storeInfo ->
                    InputChip(
                        selected = false,
                        onClick = { /* No action on click */ },
                        label = { Text("${storeInfo.name}: ${storeInfo.pivot.stock}") },
                        trailingIcon = {
                            IconButton(
                                onClick = { onRemoveFromStore(storeInfo.id) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Quitar de ${storeInfo.name}")
                            }
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (availableStores.isNotEmpty()) {
            Button(
                onClick = { onAssignToStore(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Asignar a Tienda")
            }
        } else {
            Text(
                text = "Asignado a todas las tiendas.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}