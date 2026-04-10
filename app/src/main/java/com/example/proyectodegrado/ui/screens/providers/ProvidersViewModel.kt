package com.example.proyectodegrado.ui.screens.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProvidersViewModel(
    private val providerRepository: ProviderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers.asStateFlow()

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts.asStateFlow()

    private val _selectedProductIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedProductIds: StateFlow<Set<Int>> = _selectedProductIds.asStateFlow()

    private val _isSavingProducts = MutableStateFlow(false)
    val isSavingProducts: StateFlow<Boolean> = _isSavingProducts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchProviders(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = providerRepository.getAllProviders()) {
                is ApiResult.Success -> {
                    val hydratedProviders = result.data.map { provider ->
                        when (val detailResult = providerRepository.getProvider(provider.id)) {
                            is ApiResult.Success -> detailResult.data
                            is ApiResult.Error -> provider
                        }
                    }

                    _providers.value = hydratedProviders
                    _error.value = null
                    onSuccess()
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                    onError(result.message)
                }
            }
        }
    }

    fun createProvider(
        request: ProviderRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = providerRepository.createProvider(request)) {
                is ApiResult.Success -> {
                    fetchProviders(onSuccess, onError)
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                    onError(result.message)
                }
            }
        }
    }

    fun updateProvider(
        id: Int,
        request: ProviderRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = providerRepository.updateProvider(id, request)) {
                is ApiResult.Success -> {
                    fetchProviders(onSuccess, onError)
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                    onError(result.message)
                }
            }
        }
    }

    fun deleteProvider(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val result = providerRepository.deleteProvider(id)) {
                is ApiResult.Success -> {
                    fetchProviders(onSuccess, onError)
                }

                is ApiResult.Error -> {
                    _error.value = result.message
                    onError(result.message)
                }
            }
        }
    }

    fun loadProductsForProvider(
        providerId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            if (_allProducts.value.isEmpty()) {
                when (val productsResult = productRepository.getAllProducts()) {
                    is ApiResult.Success -> {
                        _allProducts.value = productsResult.data
                    }

                    is ApiResult.Error -> {
                        _error.value = productsResult.message
                        onError(productsResult.message)
                        return@launch
                    }
                }
            }

            when (val providerResult = providerRepository.getProvider(providerId)) {
                is ApiResult.Success -> {
                    _selectedProductIds.value = providerResult.data.products.map { it.id }.toSet()
                    onSuccess()
                }

                is ApiResult.Error -> {
                    _error.value = providerResult.message
                    onError(providerResult.message)
                }
            }
        }
    }

    fun toggleProductSelection(productId: Int) {
        val current = _selectedProductIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _selectedProductIds.value = current
    }

    fun clearSelectedProducts() {
        _selectedProductIds.value = emptySet()
    }

    fun syncProviderProducts(
        providerId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSavingProducts.value = true

            when (
                val result = providerRepository.syncProviderProducts(
                    providerId = providerId,
                    productIds = _selectedProductIds.value.toList()
                )
            ) {
                is ApiResult.Success -> {
                    _isSavingProducts.value = false
                    fetchProviders(
                        onSuccess = {
                            onSuccess()
                        },
                        onError = onError
                    )
                }

                is ApiResult.Error -> {
                    _isSavingProducts.value = false
                    _error.value = result.message
                    onError(result.message)
                }
            }
        }
    }
}