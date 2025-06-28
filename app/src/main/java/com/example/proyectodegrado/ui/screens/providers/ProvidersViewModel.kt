package com.example.proyectodegrado.ui.screens.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProvidersViewModel(
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchProviders(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val providerList = providerRepository.getAllProviders()
                _providers.value = providerList
                _error.value = null
                onSuccess()
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
                onError(_error.value ?: "")
            } catch (e: HttpException) {
                _error.value = "Unexpected error: ${e.message}"
                onError(_error.value ?: "")
            } catch (e: Exception) {
                _error.value = "Unknown error: ${e.message}"
                onError(_error.value ?: "")
            }
        }
    }

    fun createProvider(
        request: ProviderRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = providerRepository.createProvider(request)
                if (response.isSuccessful) {
                    fetchProviders(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to create provider"
                    _error.value = msg
                    onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg
                onError(msg)
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
            try {
                val response = providerRepository.updateProvider(id, request)
                if (response.isSuccessful) {
                    fetchProviders(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to update provider"
                    _error.value = msg
                    onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg
                onError(msg)
            }
        }
    }

    fun deleteProvider(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = providerRepository.deleteProvider(id)
                if (response.isSuccessful) {
                    fetchProviders(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to delete provider"
                    _error.value = msg
                    onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg
                onError(msg)
            }
        }
    }
}
