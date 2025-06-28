package com.example.proyectodegrado.ui.screens.products

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun ProductItem(
    product: Product,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Aumentamos el padding para más espacio
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Fondo blanco como en CategoryItem
    ) {
        Column {
            // --- INICIO: SECCIÓN DE IMAGEN (Como en CategoryItem) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer) // Color de fondo si no hay imagen
            ) {
                // Si hay una URL de imagen, la mostramos
                if (!product.image.isNullOrBlank()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Si no hay imagen, mostramos un ícono de cámara como placeholder
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Sin imagen",
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                }

                // Pequeña etiqueta para la marca del producto
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = product.brand, // Mostramos la marca aquí
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            // --- FIN: SECCIÓN DE IMAGEN ---

            // --- INICIO: SECCIÓN DE TEXTO Y BOTONES ---
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall, // Estilo más grande para el título
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SKU: ${product.sku ?: "No definido"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
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
            // --- FIN: SECCIÓN DE TEXTO Y BOTONES ---
        }
    }
}