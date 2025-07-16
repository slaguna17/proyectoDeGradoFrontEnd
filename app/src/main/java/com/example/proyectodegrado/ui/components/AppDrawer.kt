package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.R

@Composable
fun DrawerContent(onItemSelected: (String) -> Unit) {
    val profilePic = painterResource(id = R.drawable.lemon_drink)
    val homeIcon = ImageVector.vectorResource(id = R.drawable.home)
    val productIcon = ImageVector.vectorResource(id = R.drawable.products)
    val categoryIcon = ImageVector.vectorResource(id = R.drawable.category)
    val settingsIcon = ImageVector.vectorResource(id = R.drawable.settings)
    val workersIcon = ImageVector.vectorResource(id = R.drawable.group)
    val scheduleIcon = ImageVector.vectorResource(id = R.drawable.schedule)
    val forecastIcon = ImageVector.vectorResource(id = R.drawable.bar_chart)
    val storeIcon = ImageVector.vectorResource(id = R.drawable.store)
    val balanceIcon = ImageVector.vectorResource(id = R.drawable.wallet)
    val providerIcon = ImageVector.vectorResource(id = R.drawable.truck)
    val barcodeIcon = ImageVector.vectorResource(id = R.drawable.barcode_scanner)

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ){
                Text(
                    text = "TuKiosco",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { /* Acción de clic en avatar TODO */ }) {
                    Image(
                        painter = profilePic,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            DrawerItem("Inicio", homeIcon, onItemSelected)
            DrawerItem("Productos", productIcon, onItemSelected)
            DrawerItem("Categorías", categoryIcon, onItemSelected)
            DrawerItem("Tienda", storeIcon, onItemSelected)
            DrawerItem("Empleados", workersIcon, onItemSelected)
            DrawerItem("Horarios", scheduleIcon, onItemSelected)
            DrawerItem("Pronósticos", forecastIcon, onItemSelected)
            DrawerItem("Caja", balanceIcon, onItemSelected)
            DrawerItem("Proveedores", providerIcon, onItemSelected)
            DrawerItem("Código de barras", barcodeIcon , onItemSelected)

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
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        modifier = Modifier
            .clickable { onClick(label) }
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}