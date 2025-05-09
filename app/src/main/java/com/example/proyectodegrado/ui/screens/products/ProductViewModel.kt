package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.ImageUploadResult
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _productsByCategory = MutableStateFlow<List<Product>>(emptyList())
    val productsByCategory: StateFlow<List<Product>> = _productsByCategory.asStateFlow()

    private val _createProductFormState = MutableStateFlow(CreateProductFormState())
    val createProductFormState: StateFlow<CreateProductFormState> = _createProductFormState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    fun fetchAllProducts(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ){
        viewModelScope.launch {
            try {
                val list = productRepository.getAllProducts()
                _products.value = list
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun fetchProductsByCategory(
        categoryId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val list = categoryRepository.getProductsForCategory(categoryId)
                _productsByCategory.value = list
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun createProductFromState(
        categoryId: Int,
        storeId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val s = _createProductFormState.value
        if (s.name.isBlank()) { onError("Nombre vacío"); return }
        if (s.imageUrl == null) { onError("Imagen requerida"); return }
        if (_imageUploadUiState.value !is UploadImageState.Idle) { onError("Subiendo imagen"); return }
        val data = ProductData(s.sku, s.name, s.description, s.imageUrl, categoryId, s.brand)
        val store = StoreData(storeId, s.stock.toIntOrNull() ?: 0, s.expirationDate)
        viewModelScope.launch {
            try {
                val resp = productRepository.createProduct(data, store)
                if (resp.isSuccessful) {
                    _createProductFormState.value = CreateProductFormState()
                    _imageUploadUiState.value = UploadImageState.Idle
                    fetchProductsByCategory(categoryId, onSuccess = onSuccess, onError = onError)
                } else onError("Error servidor")
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun deleteProduct(
        id: Int,
        categoryId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val resp = productRepository.deleteProduct(id)
                if (resp.isSuccessful) {
                    fetchProductsByCategory(categoryId, onSuccess = onSuccess, onError = onError)
                } else onError("Falló")
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun updateCreateProductFormState(newState: CreateProductFormState) {
        _createProductFormState.value = newState
    }

    fun handleProductImageSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val result = imageRepository.getPresignedUrlAndUpload(uri, "product", 0)) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _createProductFormState.update { it.copy(imageUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(result.message)
            }
        }
    }

}
