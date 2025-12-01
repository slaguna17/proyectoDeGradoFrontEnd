package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
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
import com.example.proyectodegrado.data.model.MenuItemDTO

data class MenuItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val isAdminOnly: Boolean = false
)

@Composable
fun DrawerContent(
    avatarUrl: String?,
    onItemSelected: (String) -> Unit,
    isAdmin: Boolean,
    menu: List<MenuItemDTO>
) {
    val mainMenuItems = listOf(
        MenuItem("Inicio", "home", ImageVector.vectorResource(id = R.drawable.home)),
        MenuItem("Productos", "products", ImageVector.vectorResource(id = R.drawable.products)),
        MenuItem("Caja", "cash", ImageVector.vectorResource(id = R.drawable.wallet)),
        MenuItem("Tienda", "store", ImageVector.vectorResource(id = R.drawable.store)),
        MenuItem("Empleados", "workers", ImageVector.vectorResource(id = R.drawable.group), isAdminOnly = true),
        MenuItem("Roles", "role", ImageVector.vectorResource(id = R.drawable.assignmentsvg), isAdminOnly = true),
        MenuItem("Horarios", "schedule", ImageVector.vectorResource(id = R.drawable.schedule), isAdminOnly = true),
        MenuItem("Categorías", "categories", ImageVector.vectorResource(id = R.drawable.category), isAdminOnly = true),
        MenuItem("Proveedores", "providers", ImageVector.vectorResource(id = R.drawable.truck), isAdminOnly = true)
    )

    val visibleMenuItems = mainMenuItems.filter { !it.isAdminOnly || isAdmin }

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

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(visibleMenuItems, key = { item -> "local_${item.route}" }) { item ->
                    DrawerItem(
                        label = item.label,
                        icon = item.icon,
                        onClick = { onItemSelected(item.route) }
                    )
                }
                items(menu, key = { dto -> "remote_${dto.id}" }) { item ->
                    DrawerItem(
                        label = item.label,
                        icon = item.icon.asVector(),
                        onClick = { onItemSelected(item.id) }
                    )
                }
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            DrawerItem(
                label = "Cerrar sesión",
                icon = Icons.Default.Logout
            ) {
                onItemSelected("logout")
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(imageVector = icon, contentDescription = null) },
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

private fun String.asVector(): ImageVector = when (this) {
    "Home" -> Icons.Default.Home
    "Package" -> Icons.Default.Inventory
    "Tag" -> Icons.Default.Label
    "Users" -> Icons.Default.Group
    "Calendar" -> Icons.Default.CalendarMonth
    "ShoppingCart" -> Icons.Default.ShoppingCart
    "Truck" -> Icons.Default.LocalShipping
    "Wallet" -> Icons.Default.AccountBalanceWallet
    "Building" -> Icons.Default.Apartment
    "Handshake" -> Icons.Default.Handshake
    "BarChart" -> Icons.Default.BarChart
    "Settings" -> Icons.Default.Settings
    "Whatsapp" -> Icons.Default.Chat
    else -> Icons.Default.Menu
}
