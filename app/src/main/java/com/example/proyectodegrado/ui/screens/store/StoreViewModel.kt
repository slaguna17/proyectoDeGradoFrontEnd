package com.example.proyectodegrado.ui.screens.store

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StoreFormState(
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val history: String = "",
    val phone: String = "",
    val localLogoUri: Uri? = null,
    val existingLogoKey: String? = null
)

class StoreViewModel(
    private val storeRepository: StoreRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _formState = MutableStateFlow(StoreFormState())
    val formState: StateFlow<StoreFormState> = _formState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    fun onNameChange(v: String)    = _formState.update { it.copy(name = v) }
    fun onAddressChange(v: String) = _formState.update { it.copy(address = v) }
    fun onCityChange(v: String)    = _formState.update { it.copy(city = v) }
    fun onHistoryChange(v: String) = _formState.update { it.copy(history = v) }
    fun onPhoneChange(v: String)   = _formState.update { it.copy(phone = v) }
    fun onPickLogo(uri: Uri?)      = _formState.update { it.copy(localLogoUri = uri) }

    fun resetForm() {
        _formState.value = StoreFormState()
        _imageUploadUiState.value = UploadImageState.Idle
    }

    fun loadStoreForEdit(store: Store) {
        _formState.value = StoreFormState(
            name = store.name,
            address = store.address,
            city = store.city,
            history = store.history,
            phone = store.phone,
            existingLogoKey = store.logo
        )
    }

    fun fetchStores(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _stores.value = storeRepository.getAllStores()
                onSuccess()
            } catch (e: Exception) {
                onError("Error al cargar tiendas: ${e.message}")
            }
        }
    }

    fun createStore(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            val initialRequest = StoreRequest(form.name, form.address, form.city, form.history, form.phone)
            val response = try {
                storeRepository.createStore(initialRequest)
            } catch (e: Exception) {
                return@launch onError("Error de red creando tienda: ${e.message}")
            }

            if (!response.isSuccessful || response.body() == null) {
                return@launch onError("El servidor no pudo crear la tienda.")
            }
            val newStore = response.body()!!

            val uri = form.localLogoUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "stores", newStore.id, "logo")

                if (uploadResult is ImageUploadResult.Success) {
                    val finalRequest = initialRequest.copy(logo = uploadResult.imageKey)
                    storeRepository.updateStore(newStore.id, finalRequest)
                } else if (uploadResult is ImageUploadResult.Error) {
                    onError("Tienda creada, pero falló la subida del logo: ${uploadResult.message}")
                }
            }

            _imageUploadUiState.value = UploadImageState.Idle
            resetForm()
            fetchStores(onSuccess)
        }
    }

    fun updateStore(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            var finalLogoKey: String? = form.existingLogoKey
            val uri = form.localLogoUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "stores", id, "logo")

                if (uploadResult is ImageUploadResult.Success) {
                    finalLogoKey = uploadResult.imageKey
                } else if (uploadResult is ImageUploadResult.Error) {
                    _imageUploadUiState.value = UploadImageState.Error(uploadResult.message)
                    return@launch onError("Falló la subida del nuevo logo: ${uploadResult.message}")
                }
            }

            val request = StoreRequest(form.name, form.address, form.city, form.history, form.phone, logo = finalLogoKey)
            try {
                storeRepository.updateStore(id, request)
                _imageUploadUiState.value = UploadImageState.Idle
                resetForm()
                fetchStores(onSuccess)
            } catch (e: Exception) {
                onError("Error al actualizar la tienda: ${e.message}")
            }
        }
    }

    fun deleteStore(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                storeRepository.deleteStore(id)
                fetchStores(onSuccess)
            } catch (e: Exception) {
                onError("Error al eliminar la tienda: ${e.message}")
            }
        }
    }
}