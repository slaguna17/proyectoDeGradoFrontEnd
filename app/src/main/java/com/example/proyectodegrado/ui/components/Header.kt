package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodegrado.R

@Composable
fun Header(navController: NavController){
    var isDrawerOpen by remember { mutableStateOf(false) }
    // TopBar
    TopAppBar(
        title = { Text("Tienda amiga") },
        actions = {
            IconButton(onClick = { isDrawerOpen = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
            }
        }
    )

    // Drawer and Background Overlay
    if (isDrawerOpen) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dim Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { isDrawerOpen = false }
            )

            // Drawer on the Right
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                        .background(Color.White)
                ) {
                    DrawerContent(
                        onItemSelected = { screen ->
                            if (screen == "Inicio"){
                                navController.navigate("home")
                            } else if (screen == "Productos"){
                                navController.navigate("products")
                            } else if (screen == "Tienda"){
                                navController.navigate("store")
                            } else if (screen == "Empleados"){
                                navController.navigate("workers")
                            } else if (screen == "Pronosticos"){
                                navController.navigate("forecast")
                            } else if (screen == "Caja"){
                                navController.navigate("balance")
                            } else if (screen == "Codigo de barras"){
                                navController.navigate("barcode")
                            } else if (screen == "Ajustes"){
                                navController.navigate("settings")
                            }
                            isDrawerOpen = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DrawerContent(onItemSelected: (String) -> Unit) {
    val profilePic = painterResource(id = R.drawable.lemon_drink)
    val homeIcon = ImageVector.vectorResource(id = R.drawable.home)
    val settingsIcon = ImageVector.vectorResource(id = R.drawable.settings)
    val workersIcon = ImageVector.vectorResource(id = R.drawable.group)
    val forecastIcon = ImageVector.vectorResource(id = R.drawable.bar_chart)
    val productsIcon = ImageVector.vectorResource(id = R.drawable.products)
    val storeIcon = ImageVector.vectorResource(id = R.drawable.store)
    val balanceIcon = ImageVector.vectorResource(id = R.drawable.wallet)
    val barcodeIcon = ImageVector.vectorResource(id = R.drawable.barcode_scanner)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ){
            Text(
                text = "Tienda amiga",
                style = MaterialTheme.typography.h6,
            )
            IconButton(onClick = {  }) {
                Image(
                    painter = profilePic,
                    contentDescription = "avatar",
                    modifier = Modifier

                        .size(40.dp)
                        .clip(CircleShape)

                )
            }


        }

        Divider()
        DrawerItem("Inicio", homeIcon, onItemSelected)
        DrawerItem("Productos", productsIcon, onItemSelected)
        DrawerItem("Tienda", storeIcon, onItemSelected)
        DrawerItem("Empleados", workersIcon, onItemSelected)
        DrawerItem("Pronosticos", forecastIcon, onItemSelected)
        DrawerItem("Caja", balanceIcon, onItemSelected)
        DrawerItem("Codigo de barras", barcodeIcon , onItemSelected)
        DrawerItem("Ajustes", settingsIcon, onItemSelected)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerItem(label: String, icon: ImageVector, onClick: (String) -> Unit) {
    ListItem(
        icon = { Icon(icon, contentDescription = null) },
        text = { Text(label) },
        modifier = Modifier
            .clickable { onClick(label) }
            .padding(vertical = 4.dp)
    )
}