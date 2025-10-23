package com.example.proyectodegrado.ui.screens.cash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodegrado.data.model.CashMovement
import com.example.proyectodegrado.data.repository.CashRepository
import com.example.proyectodegrado.ui.components.RefreshableContainer // <-- 1. IMPORTAR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashScreen(
    storeId: Int,
    userId: Int,
    repository: CashRepository = CashRepository(),
) {
    val vm = remember {
        CashViewModel.provideFactory(repository, storeId, userId)
    }.let { factory ->
        viewModel<CashViewModel>(factory = factory)
    }
    val state by vm.state.collectAsState()

    // Dialogs
    if (state.showOpenDialog) {
        OpenCashDialog(
            onDismiss = vm::hideOpenDialog,
            onConfirm = { vm.openCashbox(it) }
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
        floatingActionButton = {
            if (!state.isOpen) {
                FloatingActionButton(onClick = vm::showOpenDialog) {
                    Icon(Icons.Default.LockOpen, contentDescription = "Abrir Caja")
                }
            } else {
                ExtendedFloatingActionButton(
                    text = { Text("Cerrar Caja") },
                    icon = { Icon(Icons.Default.Close, contentDescription = "Cerrar Caja") },
                    onClick = vm::showCloseDialog
                )
            }
        }
    ) { innerpadding ->
        RefreshableContainer(
            refreshing = state.loading,
            onRefresh = vm::refresh,
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                if (!state.isOpen && !state.loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay caja abierta. Pulsa el botón para iniciar el día.")
                    }
                } else if (state.isOpen) {
                    SummaryCard(
                        opening = state.totals?.opening ?: 0.0,
                        income = state.totals?.income ?: 0.0,
                        expenses = state.totals?.expenses ?: 0.0,
                        expected = state.totals?.expectedClosing ?: 0.0,
                    )

                    Text("Movimientos del Día", style = MaterialTheme.typography.titleLarge)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.movements, key = { it.id }) { m -> MovementRow(m) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    opening: Double,
    income: Double,
    expenses: Double,
    expected: Double,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFC8E6C9)
        )) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumen del Día", style = MaterialTheme.typography.titleLarge)
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Monto de Apertura: %.2f BOB".format(opening))
            Text("Total Ingresos: + %.2f BOB".format(income))
            Text("Total Egresos: - %.2f BOB".format(expenses))
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Balance Esperado: %.2f BOB".format(expected),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = m.notes ?: m.category ?: "Movimiento",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tipo: ${m.originType ?: "Manual"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            val sign = if (m.direction == "OUT") "-" else "+"
            Text(
                text = "$sign%.2f".format(m.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = if (m.direction == "OUT") MaterialTheme.colorScheme.error else Color.White
            )
        }
    }
}