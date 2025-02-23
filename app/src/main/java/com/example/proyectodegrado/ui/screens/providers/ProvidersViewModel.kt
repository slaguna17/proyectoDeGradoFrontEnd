package com.example.proyectodegrado.ui.screens.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.repository.ProviderRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProvidersViewModel(private val providersRepository: ProviderRepository) : ViewModel() {
    //Result Messages
    private var providerResult: String = ""

    //List and state flows
    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    var providers: StateFlow<List<Provider>> = _providers

    //Single object flow
    private val emptyProvider = Provider(-1, "","", "","","", "")
    private val _provider = MutableStateFlow<Provider>(emptyProvider)

    //Provider Functions
    fun fetchProviders(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val providerList = providersRepository.getAllProviders()
                _providers.value = providerList
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun fetchProvider(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val provider = providersRepository.getProvider(id)
                _provider.value = provider
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun createProvider(request: ProviderRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = providersRepository.createProvider(request)
                if (response.isSuccessful) {
                    providerResult = response.body()?.message ?: "Created Provider successful!"
                    fetchProviders(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun updateProvider(id:Int, request: ProviderRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = providersRepository.updateProvider(id,request)
                if (response.isSuccessful) {
                    providerResult = response.body()?.message ?: "Updated Provider successfully!"
                    fetchProviders(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun deleteProvider(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = providersRepository.deleteProvider(id)
                if (response.isSuccessful) {
                    providerResult = response.body()?.message ?: "Deleted provider successfully!"
                    fetchProviders(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

}