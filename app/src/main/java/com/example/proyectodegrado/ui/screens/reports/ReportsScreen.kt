package com.example.proyectodegrado.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.PurchaseDetailItem
import com.example.proyectodegrado.data.model.PurchasesByDayItem
import com.example.proyectodegrado.data.model.PurchasesByPaymentMethodItem
import com.example.proyectodegrado.data.model.PurchasesByProviderItem
import com.example.proyectodegrado.data.model.PurchasesByStatusItem
import com.example.proyectodegrado.data.model.PurchasesSummaryResponse
import com.example.proyectodegrado.data.model.PurchasesTopProductItem
import com.example.proyectodegrado.data.model.SalesByDayItem
import com.example.proyectodegrado.data.model.SalesByPaymentMethodItem
import com.example.proyectodegrado.data.model.SalesByStatusItem
import com.example.proyectodegrado.data.model.SalesDetailItem
import com.example.proyectodegrado.data.model.SalesSummaryResponse
import com.example.proyectodegrado.data.model.SalesTopProductItem
import com.example.proyectodegrado.di.DependencyProvider
import com.example.proyectodegrado.ui.components.RefreshableContainer
import java.util.Locale

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DependencyProvider.provideReportsViewModel() as T
        }
    })
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        RefreshableContainer(
            refreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TabRow(
                    selectedTabIndex = if (state.selectedTab == ReportsTab.SALES) 0 else 1
                ) {
                    Tab(
                        selected = state.selectedTab == ReportsTab.SALES,
                        onClick = { viewModel.selectTab(ReportsTab.SALES) },
                        text = { Text("Ventas") }
                    )
                    Tab(
                        selected = state.selectedTab == ReportsTab.PURCHASES,
                        onClick = { viewModel.selectTab(ReportsTab.PURCHASES) },
                        text = { Text("Compras") }
                    )
                }

                ReportsFiltersSection(
                    fromDate = state.fromDate,
                    toDate = state.toDate,
                    onFromDateChanged = viewModel::onFromDateChanged,
                    onToDateChanged = viewModel::onToDateChanged,
                    onApply = viewModel::loadCurrentTab,
                    onClear = viewModel::clearDates
                )

                val isEmptyCurrentTab = when (state.selectedTab) {
                    ReportsTab.SALES -> state.salesSummaryResponse == null && state.salesDetails.isEmpty()
                    ReportsTab.PURCHASES -> state.purchasesSummaryResponse == null && state.purchasesDetails.isEmpty()
                }

                if (state.isLoading && isEmptyCurrentTab) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    when (state.selectedTab) {
                        ReportsTab.SALES -> SalesReportsContent(
                            summaryResponse = state.salesSummaryResponse,
                            topProducts = state.salesTopProducts,
                            details = state.salesDetails
                        )

                        ReportsTab.PURCHASES -> PurchasesReportsContent(
                            summaryResponse = state.purchasesSummaryResponse,
                            topProducts = state.purchasesTopProducts,
                            details = state.purchasesDetails,
                            byProvider = state.purchasesByProvider
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsFiltersSection(
    fromDate: String,
    toDate: String,
    onFromDateChanged: (String) -> Unit,
    onToDateChanged: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = fromDate,
                onValueChange = onFromDateChanged,
                label = { Text("Desde (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = FoundationKeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            OutlinedTextField(
                value = toDate,
                onValueChange = onToDateChanged,
                label = { Text("Hasta (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = FoundationKeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClear) {
                    Text("Limpiar")
                }

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                Button(onClick = onApply) {
                    Text("Cargar reporte")
                }
            }
        }
    }
}

@Composable
private fun SalesReportsContent(
    summaryResponse: SalesSummaryResponse?,
    topProducts: List<SalesTopProductItem>,
    details: List<SalesDetailItem>
) {
    val summary = summaryResponse?.summary

    SummaryMetricsSection(
        title = "Resumen de Ventas",
        metrics = listOf(
            "Cantidad de ventas" to (summary?.salesCount?.toString() ?: "0"),
            "Monto total" to formatMoney(summary?.totalAmount ?: 0.0),
            "Ticket promedio" to formatMoney(summary?.averageTicket ?: 0.0),
            "Cantidad vendida" to ((summary?.totalQuantity ?: 0).toString())
        )
    )

    SalesByDaySection(summaryResponse?.byDay.orEmpty())
    SalesByPaymentMethodSection(summaryResponse?.byPaymentMethod.orEmpty())
    SalesByStatusSection(summaryResponse?.byStatus.orEmpty())
    SalesTopProductsSection(topProducts)
    SalesDetailsSection(details)
}

@Composable
private fun PurchasesReportsContent(
    summaryResponse: PurchasesSummaryResponse?,
    topProducts: List<PurchasesTopProductItem>,
    details: List<PurchaseDetailItem>,
    byProvider: List<PurchasesByProviderItem>
) {
    val summary = summaryResponse?.summary

    SummaryMetricsSection(
        title = "Resumen de Compras",
        metrics = listOf(
            "Cantidad de compras" to (summary?.purchasesCount?.toString() ?: "0"),
            "Monto total" to formatMoney(summary?.totalAmount ?: 0.0),
            "Promedio por compra" to formatMoney(summary?.averagePurchase ?: 0.0),
            "Cantidad comprada" to ((summary?.totalQuantity ?: 0).toString())
        )
    )

    PurchasesByDaySection(summaryResponse?.byDay.orEmpty())
    PurchasesByPaymentMethodSection(summaryResponse?.byPaymentMethod.orEmpty())
    PurchasesByStatusSection(summaryResponse?.byStatus.orEmpty())
    PurchasesByProviderSection(byProvider)
    PurchasesTopProductsSection(topProducts)
    PurchasesDetailsSection(details)
}

@Composable
private fun SummaryMetricsSection(
    title: String,
    metrics: List<Pair<String, String>>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            metrics.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (label, value) ->
                        MetricCard(
                            label = label,
                            value = value,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SalesByDaySection(items: List<SalesByDayItem>) {
    SimpleSectionCard(title = "Ventas por día") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.date.orEmpty(),
                    secondary = "Ventas: ${item.salesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun SalesByPaymentMethodSection(items: List<SalesByPaymentMethodItem>) {
    SimpleSectionCard(title = "Ventas por método de pago") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.paymentMethod.orEmpty(),
                    secondary = "Ventas: ${item.salesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun SalesByStatusSection(items: List<SalesByStatusItem>) {
    SimpleSectionCard(title = "Ventas por estado") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.status.orEmpty(),
                    secondary = "Ventas: ${item.salesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun SalesTopProductsSection(items: List<SalesTopProductItem>) {
    SimpleSectionCard(title = "Productos más vendidos") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.productName ?: "Producto #${item.productId}",
                    secondary = "Cant: ${item.totalQuantity} • SKU: ${item.sku ?: "-"}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun SalesDetailsSection(items: List<SalesDetailItem>) {
    SimpleSectionCard(title = "Detalle de ventas") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = "Venta #${item.id}",
                    secondary = "${shortDate(item.saleDate)} • ${item.paymentMethod.orEmpty()} • ${item.status.orEmpty()}",
                    trailing = formatMoney(item.total)
                )
            }
        }
    }
}

@Composable
private fun PurchasesByDaySection(items: List<PurchasesByDayItem>) {
    SimpleSectionCard(title = "Compras por día") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.date.orEmpty(),
                    secondary = "Compras: ${item.purchasesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun PurchasesByPaymentMethodSection(items: List<PurchasesByPaymentMethodItem>) {
    SimpleSectionCard(title = "Compras por método de pago") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.paymentMethod.orEmpty(),
                    secondary = "Compras: ${item.purchasesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun PurchasesByStatusSection(items: List<PurchasesByStatusItem>) {
    SimpleSectionCard(title = "Compras por estado") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.status.orEmpty(),
                    secondary = "Compras: ${item.purchasesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun PurchasesByProviderSection(items: List<PurchasesByProviderItem>) {
    SimpleSectionCard(title = "Compras por proveedor") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.providerName ?: "Sin proveedor",
                    secondary = "Compras: ${item.purchasesCount}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun PurchasesTopProductsSection(items: List<PurchasesTopProductItem>) {
    SimpleSectionCard(title = "Productos más comprados") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = item.productName ?: "Producto #${item.productId}",
                    secondary = "Cant: ${item.totalQuantity} • SKU: ${item.sku ?: "-"}",
                    trailing = formatMoney(item.totalAmount)
                )
            }
        }
    }
}

@Composable
private fun PurchasesDetailsSection(items: List<PurchaseDetailItem>) {
    SimpleSectionCard(title = "Detalle de compras") {
        if (items.isEmpty()) {
            EmptySectionText()
        } else {
            items.forEach { item ->
                ReportLine(
                    primary = "Compra #${item.id}",
                    secondary = "${shortDate(item.purchaseDate)} • ${item.paymentMethod.orEmpty()} • ${item.status.orEmpty()}",
                    trailing = formatMoney(item.total)
                )
            }
        }
    }
}

@Composable
private fun SimpleSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun ReportLine(
    primary: String,
    secondary: String,
    trailing: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = trailing,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        HorizontalDivider()
    }
}

@Composable
private fun EmptySectionText() {
    Text(
        text = "No hay datos para mostrar con los filtros seleccionados.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun shortDate(value: String?): String {
    return value?.substringBefore("T").orEmpty()
}

private fun formatMoney(value: Double): String {
    return String.format(Locale.US, "%.2f BOB", value)
}