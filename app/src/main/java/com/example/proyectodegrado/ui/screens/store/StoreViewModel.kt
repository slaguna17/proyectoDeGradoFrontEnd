package com.example.proyectodegrado.ui.screens.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class StoreViewModel(private val storeRepository: StoreRepository) : ViewModel() {
    //Result Messages
    private var storeResult: String = ""

    //List and state flows
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    var stores: StateFlow<List<Store>> = _stores

    //Single object flow
    private val emptyStore = Store(-1, "","", "","","","")
    private val _store = MutableStateFlow<Store>(emptyStore)

    //Store Functions
    fun fetchStores(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.getAllStores()
                _stores.value = storeList
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun fetchStore(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val store = storeRepository.getStore(id)
                _store.value = store
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun createStore(request: StoreRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = storeRepository.createStore(request)
                if (response.isSuccessful) {
                    storeResult = response.body()?.message ?: "Created Store successful!"
                    fetchStores(onSuccess = onSuccess, onError = onError)
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

    fun updateStore(id:Int, request: StoreRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = storeRepository.updateStore(id,request)
                if (response.isSuccessful) {
                    storeResult = response.body()?.message ?: "Updated Store successfully!"
                    fetchStores(onSuccess = onSuccess, onError = onError)
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

    fun deleteStore(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = storeRepository.deleteStore(id)
                if (response.isSuccessful) {
                    storeResult = response.body()?.message ?: "Deleted store successfully!"
                    fetchStores(onSuccess = onSuccess, onError = onError)
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