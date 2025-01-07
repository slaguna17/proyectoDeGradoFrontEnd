package com.example.proyectodegrado.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.proyectodegrado.R
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    username: String,
    onSettingsClicked: () -> Unit,
    onMenuItemClicked: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logonobackground), // Replace with your app logo
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(
                            Icons.Filled.Menu, contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClicked) {
                        Icon(
                            Icons.Filled.Settings, contentDescription = "Settings"
                        )
                    }
                },
                backgroundColor = Color(0xFF6200EE),
                contentColor = Color.White
            )
        },
        drawerContent = {
            DrawerContent(username, onMenuItemClicked)
        }
    ) { padding ->
        // Main content of the Home Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome, $username!", style = MaterialTheme.typography.h5)
        }
    }
}

@Composable
fun DrawerContent(username: String, onMenuItemClicked: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.lemon_drink), // Replace with a user avatar image
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = username, style = MaterialTheme.typography.h6)
                Text(text = "View Profile", style = MaterialTheme.typography.body2)
            }
        }
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        DrawerMenuItem("Home", onMenuItemClicked)
        DrawerMenuItem("Users", onMenuItemClicked)
        DrawerMenuItem("Settings", onMenuItemClicked)
    }
}

@Composable
fun DrawerMenuItem(label: String, onMenuItemClicked: (String) -> Unit) {
    TextButton(
        onClick = { onMenuItemClicked(label) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "home") {
//        composable("home") { HomeScreen(username = "Sergio") { feature ->
//            navController.navigate(feature)
//        } }
//        composable("users") { UserListScreen() }
//        composable("profile") { ProfileScreen() }
//        composable("settings") { SettingsScreen() }
//    }
//}
