package com.example.proyectodegrado.ui.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.CartItem
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.SaleProductDetail
import com.example.proyectodegrado.data.model.SaleRequest
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.SalesRepository
import com.example.proyectodegrado.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SalesUiState(
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val searchQuery: String = "",
    val paymentMethod: String = "cash",
    val notes: String = "",
    val isRegistering: Boolean = false,
    val saleSuccess: Boolean = false,
    val errorMessage: String? = null
)

class SalesViewModel(
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

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
                // Si ya existe, incrementamos la cantidad
                currentState.cartItems.map {
                    if (it.productId == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                // Si no existe, lo añadimos al carrito
                currentState.cartItems + CartItem(
                    productId = product.id,
                    name = product.name,
                    unitPrice = product.price, // Asumiendo que el producto tiene un precio base
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
        _uiState.update { it.copy(cartItems = emptyList(), saleSuccess = false, errorMessage = null, notes = "") }
    }

    fun registerSale() {
        val currentState = _uiState.value
        if (currentState.cartItems.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "El carrito está vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }

            val saleRequest = SaleRequest(
                user_id = DependencyProvider.getCurrentUserId(),
                store_id = DependencyProvider.getCurrentStoreId(),
                payment_method = currentState.paymentMethod,
                notes = currentState.notes,
                products = currentState.cartItems.map {
                    SaleProductDetail(
                        product_id = it.productId,
                        quantity = it.quantity,
                        unit_price = it.unitPrice
                    )
                }
            )

            // --- CORRECCIÓN AQUÍ ---
            // Manejamos el ApiResult que ahora devuelve SalesRepository
            when (val result = salesRepository.createSale(saleRequest)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isRegistering = false, saleSuccess = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isRegistering = false, errorMessage = "Error: ${result.message}") }
                }
            }
        }
    }
}