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
    val errorMessage: String? = null,
    val draftQuantities: Map<Int, String> = emptyMap()
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
                        it.copy(
                            allProducts = products,
                            filteredProducts = products
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Error al cargar productos: ${result.message}"
                        )
                    }
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
                    it.name.contains(query, ignoreCase = true) ||
                            it.sku?.contains(query, ignoreCase = true) == true
                }
            }

            currentState.copy(
                searchQuery = query,
                filteredProducts = filtered
            )
        }
    }

    fun onDraftQuantityChanged(productId: Int, value: String) {
        val filteredValue = value.filter { it.isDigit() }

        _uiState.update { currentState ->
            currentState.copy(
                draftQuantities = currentState.draftQuantities.toMutableMap().apply {
                    this[productId] = filteredValue
                }
            )
        }
    }

    fun increaseDraftQuantity(productId: Int) {
        val currentState = _uiState.value
        val product = currentState.allProducts.find { it.id == productId } ?: return
        val availableStock = getAvailableStock(product)

        val currentDraft = currentState.draftQuantities[productId].orEmpty().toIntOrNull() ?: 0
        val newDraft = currentDraft + 1

        if (newDraft > availableStock) {
            val missingQuantity = newDraft - availableStock

            _uiState.update {
                it.copy(
                    errorMessage = "No hay stock suficiente en la tienda del producto \"${product.name}\". " +
                            "Se necesita $missingQuantity unidades para llegar a las " +
                            "$newDraft unidades requeridas. " +
                            "Stock disponible actual: $availableStock."
                )
            }
            return
        }

        _uiState.update { state ->
            state.copy(
                draftQuantities = state.draftQuantities.toMutableMap().apply {
                    this[productId] = newDraft.toString()
                }
            )
        }
    }

    fun decreaseDraftQuantity(productId: Int) {
        val currentState = _uiState.value
        val currentDraft = currentState.draftQuantities[productId].orEmpty().toIntOrNull() ?: 0
        val newDraft = (currentDraft - 1).coerceAtLeast(0)

        _uiState.update { state ->
            state.copy(
                draftQuantities = state.draftQuantities.toMutableMap().apply {
                    this[productId] = if (newDraft == 0) "" else newDraft.toString()
                }
            )
        }
    }

    fun addProductToCartWithDraftQuantity(product: Product) {
        val currentState = _uiState.value
        val typedQuantity = currentState.draftQuantities[product.id].orEmpty()
        val quantityToAdd = typedQuantity.toIntOrNull()
        val availableStock = getAvailableStock(product)

        if (quantityToAdd == null || quantityToAdd <= 0) {
            _uiState.update {
                it.copy(errorMessage = "Ingresa una cantidad válida para agregar el producto.")
            }
            return
        }

        val currentCartQuantity = currentState.cartItems
            .find { it.productId == product.id }
            ?.quantity ?: 0

        val requestedTotalQuantity = currentCartQuantity + quantityToAdd

        if (requestedTotalQuantity > availableStock) {
            val missingQuantity = requestedTotalQuantity - availableStock

            _uiState.update {
                it.copy(
                    errorMessage = "No hay stock suficiente en la tienda del producto \"${product.name}\". " +
                            "Se necesita $missingQuantity unidades para llegar a las " +
                            "$requestedTotalQuantity unidades requeridas. " +
                            "Stock disponible actual: $availableStock."
                )
            }
            return
        }

        _uiState.update { state ->
            val existingItem = state.cartItems.find { it.productId == product.id }

            val updatedCartItems = if (existingItem != null) {
                state.cartItems.map {
                    if (it.productId == product.id) {
                        it.copy(quantity = it.quantity + quantityToAdd)
                    } else {
                        it
                    }
                }
            } else {
                state.cartItems + CartItem(
                    productId = product.id,
                    name = product.name,
                    unitPrice = product.salePrice,
                    quantity = quantityToAdd
                )
            }

            val updatedDrafts = state.draftQuantities.toMutableMap().apply {
                this[product.id] = ""
            }

            state.copy(
                cartItems = updatedCartItems,
                draftQuantities = updatedDrafts
            )
        }
    }

    fun increaseCartItemQuantity(productId: Int) {
        val currentState = _uiState.value
        val cartItem = currentState.cartItems.find { it.productId == productId } ?: return
        val product = currentState.allProducts.find { it.id == productId } ?: return
        val availableStock = getAvailableStock(product)

        val newQuantity = cartItem.quantity + 1

        if (newQuantity > availableStock) {
            val missingQuantity = newQuantity - availableStock

            _uiState.update {
                it.copy(
                    errorMessage = "No hay stock suficiente en la tienda del producto \"${product.name}\". " +
                            "Se necesita $missingQuantity unidades para llegar a las " +
                            "$newQuantity unidades requeridas. " +
                            "Stock disponible actual: $availableStock."
                )
            }
            return
        }

        _uiState.update { state ->
            val updated = state.cartItems.map {
                if (it.productId == productId) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
            state.copy(cartItems = updated)
        }
    }

    fun decreaseCartItemQuantity(productId: Int) {
        val currentItem = _uiState.value.cartItems.find { it.productId == productId } ?: return

        if (currentItem.quantity <= 1) {
            removeCartItem(productId)
            return
        }

        _uiState.update { currentState ->
            val updated = currentState.cartItems.map {
                if (it.productId == productId) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }
            currentState.copy(cartItems = updated)
        }
    }

    fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeCartItem(productId)
            return
        }

        _uiState.update { currentState ->
            val newCartItems = currentState.cartItems.map {
                if (it.productId == productId) {
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
            currentState.copy(cartItems = newCartItems)
        }
    }

    fun removeCartItem(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                cartItems = currentState.cartItems.filterNot { it.productId == productId }
            )
        }
    }

    fun onPaymentMethodChanged(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun onNotesChanged(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun clearCart() {
        _uiState.update {
            it.copy(
                cartItems = emptyList(),
                saleSuccess = false,
                errorMessage = null,
                notes = "",
                draftQuantities = emptyMap()
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun registerSale() {
        val currentState = _uiState.value

        if (currentState.cartItems.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "El carrito está vacío")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRegistering = true,
                    errorMessage = null
                )
            }

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

            when (val result = salesRepository.createSale(saleRequest)) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            saleSuccess = true
                        )
                    }
                }

                is ApiResult.Error -> {
                    val friendlyMessage = SalesErrorMapper.mapCreateSaleError(result.message)

                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            errorMessage = friendlyMessage
                        )
                    }
                }
            }
        }
    }

    private fun getAvailableStock(product: Product): Int {
        val currentStoreId = DependencyProvider.getCurrentStoreId()

        return product.stock
            ?: product.stores
                ?.firstOrNull { it.id == currentStoreId }
                ?.pivot
                ?.stock
            ?: 0
    }
}