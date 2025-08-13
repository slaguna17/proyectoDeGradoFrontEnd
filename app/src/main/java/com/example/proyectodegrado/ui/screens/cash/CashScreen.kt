package com.example.proyectodegrado.ui.screens.cash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectodegrado.data.model.CashMovement
import com.example.proyectodegrado.data.repository.CashRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashScreen(
    storeId: Int,
    userId: Int,
    repository: CashRepository = CashRepository(), // inyección manual simple
) {
    val vm = remember {
        CashViewModel.provideFactory(repository, storeId, userId)
    }.let { factory ->
        androidx.lifecycle.viewmodel.compose.viewModel<CashViewModel>(factory = factory)
    }

    val state by vm.state.collectAsState()

    // Diálogos
    if (state.showOpenDialog) {
        OpenCashDialog(
            onDismiss = vm::hideOpenDialog,
            onConfirm = { vm.openCashbox(it) }
        )
    }
    if (state.showMovementDialog) {
        MovementDialog(
            onDismiss = vm::hideMovementDialog,
            onConfirm = { direction, amount, category, notes ->
                vm.createMovement(direction, amount, category, notes)
            }
        )
    }
    if (state.showCloseDialog) {
        val expected = state.totals?.expectedClosing ?: 0.0
        CloseCashDialog(
            expected = expected,
            onDismiss = vm::hideCloseDialog,
            onConfirm = { vm.closeCashbox(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caja") },
                actions = {
                    IconButton(onClick = vm::refresh) { Icon(Icons.Default.Refresh, contentDescription = "Refrescar") }
                }
            )
        },
        floatingActionButton = {
            if (!state.isOpen) {
                FloatingActionButton(onClick = vm::showOpenDialog) { Text("Abrir") }
            } else {
                ExtendedFloatingActionButton(
                    onClick = vm::showMovementDialog,
                    text = { Text("Movimiento") }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            // Resumen / sin sesión
            if (!state.isOpen) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay caja abierta. Pulsa \"Abrir\" para iniciar el día.")
                }
            } else {
                SummaryCard(
                    isOpen = state.isOpen,
                    opening = state.totals?.opening ?: 0.0,
                    sales = state.totals?.salesCash ?: 0.0,
                    purchases = state.totals?.purchasesCash ?: 0.0,
                    inManual = state.totals?.manualIn ?: 0.0,
                    outManual = state.totals?.manualOut ?: 0.0,
                    expected = state.totals?.expectedClosing ?: 0.0,
                    closing = state.totals?.closingAmount
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = vm::showMovementDialog) { Text("Agregar movimiento") }
                    Button(onClick = vm::showCloseDialog, enabled = state.isOpen) { Text("Cerrar caja") }
                }

                Text("Movimientos", style = MaterialTheme.typography.titleMedium)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.movements) { m -> MovementRow(m) }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    isOpen: Boolean,
    opening: Double,
    sales: Double,
    purchases: Double,
    inManual: Double,
    outManual: Double,
    expected: Double,
    closing: Double?
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (isOpen) "Caja abierta" else "Caja cerrada", fontWeight = FontWeight.Bold)
            Text("Apertura: %.2f".format(opening))
            Text("Ventas (efectivo): %.2f".format(sales))
            Text("Compras (efectivo): %.2f".format(purchases))
            Text("Ingresos manuales: %.2f".format(inManual))
            Text("Egresos manuales: %.2f".format(outManual))
            Divider()
            Text("Cierre esperado: %.2f".format(expected), fontWeight = FontWeight.SemiBold)
            closing?.let {
                val diff = it - expected
                Text("Cierre declarado: %.2f".format(it))
                Text("Diferencia: %+.2f".format(diff))
            }
        }
    }
}

@Composable
private fun MovementRow(m: CashMovement) {
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(m.direction + (m.category?.let { " · $it" } ?: ""))
                m.notes?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
            val sign = if (m.direction == "OUT") -1 else 1
            Text("%+.2f".format(sign * m.amount), fontWeight = FontWeight.SemiBold)
        }
    }
}
