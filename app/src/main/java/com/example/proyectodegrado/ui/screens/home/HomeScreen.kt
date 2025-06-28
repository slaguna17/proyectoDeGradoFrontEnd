package com.example.proyectodegrado.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.proyectodegrado.ui.screens.products.AllProductsScreen
import com.example.proyectodegrado.ui.screens.products.ProductViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ProductViewModel // <-- CAMBIO 1: Recibimos el ViewModel como parámetro
) {
    // Ya no necesitamos obtener el ViewModel desde DependencyProvider aquí.

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // CAMBIO 2: Usamos el viewModel que recibimos.
        AllProductsScreen(
            navController = navController,
            viewModel = viewModel
        )
    }
}