package com.example.proyectodegrado.ui.screens.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.CartItem
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.PurchaseProductDetail
import com.example.proyectodegrado.data.model.PurchaseRequest
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.PurchasesRepository
import com.example.proyectodegrado.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus

data class PurchasesUiState(
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val searchQuery: String = "",
    val paymentMethod: String = "cash",
    val notes: String = "",
    val isRegistering: Boolean = false,
    val purchaseSuccess: Boolean = false,
    val errorMessage: String? = null
)
class PurchasesViewModel (
    private val purchasesRepository: PurchasesRepository,
    private val productRepository: ProductRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(PurchasesUiState())
    val uiState: StateFlow<PurchasesUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            when (val result = productRepository.getAllProducts()) {
                is ApiResult.Success -> {
                    val products = result.data
                    _uiState.update {
                        it.copy(allProducts = products, filteredProducts = products)
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(errorMessage = "Error al cargar productos: ${result.message}") }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { currentState ->
            val filtered = if (query.isBlank()) {
                currentState.allProducts
            } else {
                currentState.allProducts.filter {
                    it.name.contains(query, ignoreCase = true) || it.sku?.contains(query, ignoreCase = true) == true
                }
            }
            currentState.copy(searchQuery = query, filteredProducts = filtered)
        }
    }

    fun addProductToCart(product: Product) {
        _uiState.update { currentState ->
            val existingItem = currentState.cartItems.find { it.productId == product.id }
            val newCartItems = if (existingItem != null) {
                currentState.cartItems.map {
                    if (it.productId == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentState.cartItems + CartItem(
                    productId = product.id,
                    name = product.name,
                    unitPrice = product.purchasePrice,
                    quantity = 1
                )
            }
            currentState.copy(cartItems = newCartItems)
        }
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(productId)
            return
        }
        _uiState.update { currentState ->
            val newCartItems = currentState.cartItems.map {
                if (it.productId == productId) it.copy(quantity = newQuantity) else it
            }
            currentState.copy(cartItems = newCartItems)
        }
    }

    fun removeCartItem(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(cartItems = currentState.cartItems.filterNot { it.productId == productId })
        }
    }

    fun onPaymentMethodChanged(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun onNotesChanged(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList(), purchaseSuccess = false, errorMessage = null, notes = "") }
    }

    fun registerPurchase() {
        val currentState = _uiState.value
        if (currentState.cartItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "El carrito está vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }

            val purchaseRequest = PurchaseRequest(
                user_id = DependencyProvider.getCurrentUserId(),
                store_id = DependencyProvider.getCurrentStoreId(),
                payment_method = currentState.paymentMethod,
                notes = currentState.notes,
                products = currentState.cartItems.map {
                    PurchaseProductDetail(
                        product_id = it.productId,
                        quantity = it.quantity,
                        unit_price = it.unitPrice
                    )
                }
            )
            when (val result = purchasesRepository.createPurchase(purchaseRequest)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isRegistering = false, purchaseSuccess = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isRegistering = false, errorMessage = "Error: ${result.message}") }
                }
            }
        }
    }
}