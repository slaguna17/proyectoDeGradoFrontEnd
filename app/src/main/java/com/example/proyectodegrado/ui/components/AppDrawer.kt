package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectodegrado.R

@Composable
fun DrawerContent(
    onItemSelected: (String) -> Unit,
    avatarUrl: String? = null        // <- Avatar URL (can be null)
) {
    val homeIcon = ImageVector.vectorResource(id = R.drawable.home)
    val productIcon = ImageVector.vectorResource(id = R.drawable.products)
    val categoryIcon = ImageVector.vectorResource(id = R.drawable.category)
    val settingsIcon = ImageVector.vectorResource(id = R.drawable.settings)
    val workersIcon = ImageVector.vectorResource(id = R.drawable.group)
    val scheduleIcon = ImageVector.vectorResource(id = R.drawable.schedule)
    val storeIcon = ImageVector.vectorResource(id = R.drawable.store)
    val roleIcon = ImageVector.vectorResource(id = R.drawable.assignmentsvg)
    val balanceIcon = ImageVector.vectorResource(id = R.drawable.wallet)
    val providerIcon = ImageVector.vectorResource(id = R.drawable.truck)

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "TuKiosco",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { onItemSelected("profile") }) {
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            DrawerItem("Inicio", homeIcon, onItemSelected)
            DrawerItem("Productos", productIcon, onItemSelected)
            DrawerItem("Categorías", categoryIcon, onItemSelected)
            DrawerItem("Tienda", storeIcon, onItemSelected)
            DrawerItem("Roles", roleIcon, onItemSelected)
            DrawerItem("Empleados", workersIcon, onItemSelected)
            DrawerItem("Horarios", scheduleIcon, onItemSelected)
            DrawerItem("Caja", balanceIcon, onItemSelected)
            DrawerItem("Proveedores", providerIcon, onItemSelected)

            Spacer(Modifier.weight(1f))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem("Ajustes", settingsIcon, onItemSelected)
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: (String) -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier
            .clickable { onClick(label) }
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
