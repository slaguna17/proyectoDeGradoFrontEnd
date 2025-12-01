package com.example.proyectodegrado.ui.screens.whatsapp_sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.ShoppingCart
import com.example.proyectodegrado.data.repository.ShoppingCartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WhatsappSalesUiState(
    val carts: List<ShoppingCart> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class WhatsappSalesViewModel(
    private val repository: ShoppingCartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatsappSalesUiState())
    val uiState: StateFlow<WhatsappSalesUiState> = _uiState.asStateFlow()

    fun loadCarts(storeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.getCartsByStore(storeId)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(carts = result.data, isLoading = false)
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al cargar pedidos: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateItemQuantity(cartId: Int, productId: Int, newQuantity: Int) {
        val currentCarts = _uiState.value.carts
        val cartIndex = currentCarts.indexOfFirst { it.id == cartId }
        if (cartIndex == -1) return

        val cart = currentCarts[cartIndex]
        val updatedItems = cart.items.map { item ->
            if (item.productId == productId) item.copy(quantity = newQuantity) else item
        }

        val newTotal = updatedItems.sumOf { it.quantity * it.unitPrice }
        val updatedCart = cart.copy(items = updatedItems, totalEstimated = newTotal)

        val updatedList = currentCarts.toMutableList()
        updatedList[cartIndex] = updatedCart

        _uiState.update { it.copy(carts = updatedList) }

        viewModelScope.launch {
            repository.updateCart(cartId, updatedItems)
        }
    }

    fun finalizeSale(cartId: Int, userId: Int, storeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.finalizeCart(cartId, userId, "CASH")) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Venta registrada con éxito"
                        )
                    }
                    loadCarts(storeId)
                }
                is ApiResult.Error -> {
                    val friendly = mapFinalizeSaleError(result.message)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendly
                        )
                    }
                }
            }
        }
    }

    private fun mapFinalizeSaleError(serverMessage: String?): String {
        if (serverMessage?.contains(
                "There is not an opened cashbox session",
                ignoreCase = true
            ) == true
        ) {
            return "No hay una caja abierta. Debes abrir una caja para registrar la venta."
        }
        return serverMessage ?: "Ocurrió un error al cobrar el pedido."
    }

    fun deleteCart(cartId: Int, storeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.deleteCart(cartId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Pedido rechazado/eliminado") }
                    loadCarts(storeId)
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error al eliminar: ${result.message}")
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}