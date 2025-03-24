package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.data.model.Category

@Composable
fun CategoryItem(
    category: Category,
    navController: NavController,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            //Navigate to appropiate products
            .clickable {
                navController.navigate("products/${category.id}")
            }
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                // Image section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = category.image,
                        contentDescription = "Category Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Content section
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ID: ${category.id}",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = category.name,
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
                        text = category.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add a "Read more" option if description is long
                    if (category.description.length > 100) {
                        Text(
                            text = "Read more",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.clickable { /* Add action here */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onEdit(category) },
                            modifier = Modifier.padding(end = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colors.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar")
                        }

                        Button(
                            onClick = { onDelete(category) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}