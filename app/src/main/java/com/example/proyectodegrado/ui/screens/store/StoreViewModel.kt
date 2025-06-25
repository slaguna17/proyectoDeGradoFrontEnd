package com.example.proyectodegrado.ui.screens.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class StoreViewModel(
    private val storeRepository: StoreRepository,
    private val imageRepository: ImageRepository // Si quieres manejo de logos subidos, como en categorías
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Cargar todas las tiendas
    fun fetchStores(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.getAllStores()
                _stores.value = storeList
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

    fun createStore(
        request: StoreRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = storeRepository.createStore(request)
                if (response.isSuccessful) {
                    fetchStores(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to create store"
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

    fun updateStore(
        id: Int,
        request: StoreRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = storeRepository.updateStore(id, request)
                if (response.isSuccessful) {
                    fetchStores(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to update store"
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

    fun deleteStore(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = storeRepository.deleteStore(id)
                if (response.isSuccessful) {
                    fetchStores(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to delete store"
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


    // fun handleStoreLogoSelection(uri: Uri?) { ... }

}
