package com.example.proyectodegrado.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.PurchaseDetailItem
import com.example.proyectodegrado.data.model.PurchasesByProviderItem
import com.example.proyectodegrado.data.model.PurchasesSummaryResponse
import com.example.proyectodegrado.data.model.PurchasesTopProductItem
import com.example.proyectodegrado.data.model.SalesDetailItem
import com.example.proyectodegrado.data.model.SalesSummaryResponse
import com.example.proyectodegrado.data.model.SalesTopProductItem
import com.example.proyectodegrado.data.repository.ReportsRepository
import com.example.proyectodegrado.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ReportsTab {
    SALES,
    PURCHASES
}

data class ReportsUiState(
    val selectedTab: ReportsTab = ReportsTab.SALES,
    val fromDate: String = "",
    val toDate: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,

    val salesSummaryResponse: SalesSummaryResponse? = null,
    val salesDetails: List<SalesDetailItem> = emptyList(),
    val salesTopProducts: List<SalesTopProductItem> = emptyList(),

    val purchasesSummaryResponse: PurchasesSummaryResponse? = null,
    val purchasesDetails: List<PurchaseDetailItem> = emptyList(),
    val purchasesTopProducts: List<PurchasesTopProductItem> = emptyList(),
    val purchasesByProvider: List<PurchasesByProviderItem> = emptyList()
)

class ReportsViewModel(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentTab()
    }

    fun onFromDateChanged(value: String) {
        _uiState.update { it.copy(fromDate = value) }
    }

    fun onToDateChanged(value: String) {
        _uiState.update { it.copy(toDate = value) }
    }

    fun clearDates() {
        _uiState.update {
            it.copy(
                fromDate = "",
                toDate = ""
            )
        }
        loadCurrentTab()
    }

    fun selectTab(tab: ReportsTab) {
        val previous = _uiState.value.selectedTab
        if (previous == tab) return

        _uiState.update { it.copy(selectedTab = tab) }

        val shouldLoad = when (tab) {
            ReportsTab.SALES -> _uiState.value.salesSummaryResponse == null
            ReportsTab.PURCHASES -> _uiState.value.purchasesSummaryResponse == null
        }

        if (shouldLoad) {
            loadCurrentTab()
        }
    }

    fun refresh() {
        loadCurrentTab(isRefresh = true)
    }

    fun loadCurrentTab() {
        loadCurrentTab(isRefresh = false)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loadCurrentTab(isRefresh: Boolean) {
        val currentState = _uiState.value

        if (!validateDates(currentState.fromDate, currentState.toDate)) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            val storeId = DependencyProvider.getCurrentStoreId()

            when (_uiState.value.selectedTab) {
                ReportsTab.SALES -> loadSales(storeId)
                ReportsTab.PURCHASES -> loadPurchases(storeId)
            }
        }
    }

    private suspend fun loadSales(storeId: Int) {
        val state = _uiState.value

        when (
            val result = reportsRepository.getSalesReport(
                storeId = storeId,
                from = state.fromDate,
                to = state.toDate
            )
        ) {
            is ApiResult.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        salesSummaryResponse = result.data.summary,
                        salesDetails = result.data.details.details,
                        salesTopProducts = result.data.topProducts.products
                    )
                }
            }

            is ApiResult.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    private suspend fun loadPurchases(storeId: Int) {
        val state = _uiState.value

        when (
            val result = reportsRepository.getPurchasesReport(
                storeId = storeId,
                from = state.fromDate,
                to = state.toDate
            )
        ) {
            is ApiResult.Success -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        purchasesSummaryResponse = result.data.summary,
                        purchasesDetails = result.data.details.details,
                        purchasesTopProducts = result.data.topProducts.products,
                        purchasesByProvider = result.data.byProvider.providers
                    )
                }
            }

            is ApiResult.Error -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    private fun validateDates(from: String, to: String): Boolean {
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")

        if (from.isNotBlank() && !regex.matches(from)) {
            _uiState.update {
                it.copy(errorMessage = "La fecha desde debe tener formato YYYY-MM-DD.")
            }
            return false
        }

        if (to.isNotBlank() && !regex.matches(to)) {
            _uiState.update {
                it.copy(errorMessage = "La fecha hasta debe tener formato YYYY-MM-DD.")
            }
            return false
        }

        if (from.isNotBlank() && to.isNotBlank() && from > to) {
            _uiState.update {
                it.copy(errorMessage = "La fecha desde no puede ser mayor que la fecha hasta.")
            }
            return false
        }

        return true
    }
}