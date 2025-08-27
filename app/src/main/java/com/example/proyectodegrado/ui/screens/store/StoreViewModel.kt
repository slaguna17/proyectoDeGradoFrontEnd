package com.example.proyectodegrado.ui.screens.store

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.ImageUploadResult
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class StoreViewModel(
    private val storeRepository: StoreRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ---- Estado para imágenes ----
    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState

    private val _createLogoKey = MutableStateFlow<String?>(null)
    val createLogoKey: StateFlow<String?> = _createLogoKey

    private val _editLogoKey = MutableStateFlow<String?>(null)
    val editLogoKey: StateFlow<String?> = _editLogoKey

    // Cargar todas las tiendas
    fun fetchStores(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _stores.value = storeRepository.getAllStores()
                _error.value = null
                onSuccess()
            } catch (e: IOException) { _error.value = "Network error: ${e.message}"; onError(_error.value ?: "") }
            catch (e: HttpException) { _error.value = "Unexpected error: ${e.message()}"; onError(_error.value ?: "") }
            catch (e: Exception) { _error.value = "Unknown error: ${e.message}"; onError(_error.value ?: "") }
        }
    }

    // ---- Subida de LOGO (crear) → stores/0 ----
    fun handleStoreLogoSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "stores", 0)) {
                is ImageUploadResult.Success -> {
                    _createLogoKey.value = r.imageKey
                    _imageUploadUiState.value = UploadImageState.Idle
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(r.message)
            }
        }
    }

    // ---- Subida de LOGO (editar) → stores/{id} ----
    fun selectLogoForEdit(storeId: Int, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "stores", storeId)) {
                is ImageUploadResult.Success -> {
                    _editLogoKey.value = r.imageKey
                    _imageUploadUiState.value = UploadImageState.Idle
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(r.message)
            }
        }
    }
    fun clearEditLogoKey() { _editLogoKey.value = null }
    fun clearCreateLogoKey() { _createLogoKey.value = null }

    fun createStore(
        request: StoreRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Si el usuario subió un logo, usa la KEY; si no, respeta request.logo (URL externa)
                val finalReq = request.copy(
                    logoKey = request.logoKey ?: _createLogoKey.value
                )
                val response = storeRepository.createStore(finalReq)
                if (response.isSuccessful) {
                    clearCreateLogoKey()
                    fetchStores(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to create store"
                    _error.value = msg; onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg; onError(msg)
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
                // Prioriza la KEY nueva si el usuario cambió el logo
                val finalReq = request.copy(
                    logoKey = _editLogoKey.value ?: request.logoKey
                )
                val response = storeRepository.updateStore(id, finalReq)
                if (response.isSuccessful) {
                    clearEditLogoKey()
                    fetchStores(onSuccess, onError)
                } else {
                    val msg = response.errorBody()?.string() ?: "Failed to update store"
                    _error.value = msg; onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg; onError(msg)
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
                if (response.isSuccessful) fetchStores(onSuccess, onError)
                else {
                    val msg = response.errorBody()?.string() ?: "Failed to delete store"
                    _error.value = msg; onError(msg)
                }
            } catch (e: Exception) {
                val msg = "Error: ${e.message}"
                _error.value = msg; onError(msg)
            }
        }
    }
}
