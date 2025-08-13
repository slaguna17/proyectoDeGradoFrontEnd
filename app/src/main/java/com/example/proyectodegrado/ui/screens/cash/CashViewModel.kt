package com.example.proyectodegrado.ui.screens.cash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CashUiState(
    val loading: Boolean = false,
    val error: String? = null,

    val currentSession: CashboxSession? = null,
    val totals: CashTotals? = null,
    val movements: List<CashMovement> = emptyList(),

    val isOpen: Boolean = false,

    // dialogs
    val showOpenDialog: Boolean = false,
    val showMovementDialog: Boolean = false,
    val showCloseDialog: Boolean = false
)

class CashViewModel(
    private val repo: CashRepository,
    private val storeId: Int,
    private val userId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(CashUiState(loading = true))
    val state: StateFlow<CashUiState> = _state

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.getCurrent(storeId)) {
            is ApiResult.Success -> {
                val data = res.data
                // Cargar movimientos de la sesión actual
                val moves = loadMovementsSafely(data.session.id)
                _state.value = _state.value.copy(
                    loading = false,
                    currentSession = data.session,
                    totals = data.totals,
                    movements = moves,
                    isOpen = data.session.status.equals("open", ignoreCase = true)
                )
            }
            is ApiResult.Error -> {
                // 204 No Content cuando no hay caja abierta → lo tratamos como "sin sesión"
                _state.value = _state.value.copy(
                    loading = false,
                    currentSession = null,
                    totals = null,
                    movements = emptyList(),
                    isOpen = false,
                    error = if (res.code == 204) null else res.message
                )
            }
        }
    }

    private suspend fun loadMovementsSafely(sessionId: Int): List<CashMovement> {
        return when (val mr = repo.getSessionMovements(sessionId)) {
            is ApiResult.Success -> mr.data.movements
            is ApiResult.Error -> emptyList()
        }
    }

    // --- Abrir caja ---
    fun openCashbox(openingAmount: Double) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val res = repo.openCashbox(storeId, openingAmount)) {
            is ApiResult.Success -> {
                refresh()
                hideOpenDialog()
            }
            is ApiResult.Error -> _state.value = _state.value.copy(
                loading = false,
                error = res.message
            )
        }
    }

    // --- Crear movimiento ---
    fun createMovement(direction: String, amount: Double, category: String?, notes: String?) =
        viewModelScope.launch {
            val date = LocalDate.now().toString()
            _state.value = _state.value.copy(loading = true, error = null)
            when (val res = repo.createMovement(storeId, userId, direction, amount, category, notes, date)) {
                is ApiResult.Success -> {
                    // recargar movimientos y totales
                    refresh()
                    hideMovementDialog()
                }
                is ApiResult.Error -> _state.value = _state.value.copy(
                    loading = false,
                    error = res.message
                )
            }
        }

    // --- Cerrar caja ---
    fun closeCashbox(closingAmount: Double? = null, cashCount: List<CashCountItem>? = null) =
        viewModelScope.launch {
            val date = LocalDate.now().toString()
            _state.value = _state.value.copy(loading = true, error = null)
            when (val res = repo.closeCashbox(storeId, userId, date, closingAmount, cashCount)) {
                is ApiResult.Success -> {
                    refresh()
                    hideCloseDialog()
                }
                is ApiResult.Error -> _state.value = _state.value.copy(
                    loading = false,
                    error = res.message
                )
            }
        }

    // --- Diálogos ---
    fun showOpenDialog()  { _state.value = _state.value.copy(showOpenDialog = true) }
    fun hideOpenDialog()  { _state.value = _state.value.copy(showOpenDialog = false) }
    fun showMovementDialog() { _state.value = _state.value.copy(showMovementDialog = true) }
    fun hideMovementDialog() { _state.value = _state.value.copy(showMovementDialog = false) }
    fun showCloseDialog() { _state.value = _state.value.copy(showCloseDialog = true) }
    fun hideCloseDialog() { _state.value = _state.value.copy(showCloseDialog = false) }

    // --- Factory manual (sin Hilt) ---
    companion object {
        fun provideFactory(
            repo: CashRepository,
            storeId: Int,
            userId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CashViewModel(repo, storeId, userId) as T
            }
        }
    }
}
