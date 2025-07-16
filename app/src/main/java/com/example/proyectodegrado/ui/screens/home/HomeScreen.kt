package com.example.proyectodegrado.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.di.AppPreferences

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val userName = remember { AppPreferences(context).getUserName() ?: "Usuario" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👋 ¡Bienvenido, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Guía rápida para comenzar!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(28.dp))

        QuickGuideSteps()

        Spacer(Modifier.height(36.dp))
        Text(
            text = "¿Necesitas ayuda? Consulta el menú lateral.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun QuickGuideSteps() {
    val steps = listOf(
        "Selecciona y crea tu tienda en la parte superior.",
        "Agrega productos usando el botón 'Crear Producto'.",
        "Administra empleados y horarios desde el menú.",
        "Consulta ventas, compras y gestiona inventario.",
        "Usa el buscador para encontrar productos o empleados."
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        steps.forEachIndexed { i, step ->
            StepItem(number = i + 1, text = step)
        }
    }
}

@Composable
fun StepItem(number: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(number.toString(), style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}
