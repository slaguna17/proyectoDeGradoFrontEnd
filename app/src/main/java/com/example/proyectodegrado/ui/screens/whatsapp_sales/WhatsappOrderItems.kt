package com.example.proyectodegrado.ui.screens.whatsapp_sales

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.ReservedCartItem
import com.example.proyectodegrado.data.model.ShoppingCart

@Composable
fun WhatsappOrderItem(
    cart: ShoppingCart,
    onIncreaseQty: (productId: Int, currentQty: Int) -> Unit,
    onDecreaseQty: (productId: Int, currentQty: Int) -> Unit,
    onFinalizeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(text = cart.customerName ?: "Cliente Desconocido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(text = cart.customerPhone, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Bs. %.2f".format(cart.totalEstimated), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = "Detalles")
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(text = "Productos Solicitados:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))

                    cart.items.forEach { item ->
                        WhatsappCartProductRow(
                            item = item, // item es ReservedCartItem
                            onIncrease = { onIncreaseQty(item.productId, item.quantity) },
                            onDecrease = { onDecreaseQty(item.productId, item.quantity) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        OutlinedButton(onClick = onDeleteClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Rechazar")
                        }
                        Button(onClick = onFinalizeClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cobrar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsappCartProductRow(
    item: ReservedCartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!item.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp))
            )
        } else {
            Box(modifier = Modifier.size(50.dp).background(Color.LightGray, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                Text("IMG", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.productName ?: "Producto ID ${item.productId}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = "PU: Bs. ${item.unitPrice}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
            }
        }
    }
}