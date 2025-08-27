package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.ImageUploadResult
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _productsByCategory = MutableStateFlow<List<Product>>(emptyList())
    val productsByCategory: StateFlow<List<Product>> = _productsByCategory.asStateFlow()

    private val _availableCategories = MutableStateFlow<List<Category>>(emptyList())
    val availableCategories: StateFlow<List<Category>> = _availableCategories.asStateFlow()

    private val _createProductFormState = MutableStateFlow(CreateProductFormState())
    val createProductFormState: StateFlow<CreateProductFormState> = _createProductFormState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    private val _stores = MutableStateFlow<List<StoreOption>>(emptyList())
    val stores: StateFlow<List<StoreOption>> = _stores.asStateFlow()

    private val _selectedStoreId = MutableStateFlow<Int?>(null)
    val selectedStoreId: StateFlow<Int?> = _selectedStoreId.asStateFlow()

    private val _editImageKey = MutableStateFlow<String?>(null)
    val editImageKey: StateFlow<String?> = _editImageKey.asStateFlow()

    init {
        fetchAvailableCategories()
        // No forzamos tienda por defecto aquí; se puede setear desde la pantalla según AppPreferences
    }

    private fun fetchAvailableCategories() {
        viewModelScope.launch {
            try {
                _availableCategories.value = categoryRepository.getAllCategories()
            } catch (e: Exception) {
                println("Error fetching categories for ViewModel: ${e.message}")
            }
        }
    }

    fun fetchStores(onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val fetched = storeRepository.getAllStores()
                _stores.value = fetched.map { StoreOption(it.id, it.name) }
            } catch (e: IOException) { onError("Red (tiendas): ${e.message}") }
            catch (e: HttpException) { onError("HTTP (tiendas): ${e.message}") }
            catch (e: Exception) { onError("Error (tiendas): ${e.message}") }
        }
    }

    fun setSelectedStore(storeId: Int?) {
        _selectedStoreId.value = storeId
    }

    // --- LISTADOS CON FILTRO POR TIENDA ---
    fun fetchAllProducts(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val storeId = _selectedStoreId.value
                _products.value = if (storeId == null) {
                    productRepository.getAllProducts()
                } else {
                    productRepository.getProductsByStore(storeId)
                }
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Error: ${e.message}") }
        }
    }

    fun fetchProductsByCategory(
        categoryId: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val base = productRepository.getProductsByCategory(categoryId)
                val storeId = _selectedStoreId.value
                _productsByCategory.value = if (storeId == null) {
                    base
                } else {
                    // Filtramos localmente por tienda usando la relación stores[].pivot.stock
                    base.filter { product ->
                        product.stores?.any { it.id == storeId } == true
                    }
                }
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Error: ${e.message}") }
        }
    }

    fun createProductFromState(storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = _createProductFormState.value
        if (s.name.isBlank() || s.sku.isBlank() || s.brand.isBlank() || s.categoryId <= 0) {
            onError("Los campos Nombre, SKU, Marca y Categoría son requeridos.")
            return
        }
        if (_imageUploadUiState.value is UploadImageState.Loading) {
            onError("Por favor, espere a que la imagen termine de subirse.")
            return
        }

        val request = ProductRequest(
            sku = s.sku,
            name = s.name,
            description = s.description,
            imageKey = s.imageKey,
            brand = s.brand,
            categoryId = s.categoryId,
            storeId = storeId,
            stock = s.stock.toIntOrNull() ?: 0,
            expirationDate = "No aplica"
        )
        viewModelScope.launch {
            try {
                val resp = productRepository.createProduct(request)
                if (resp.isSuccessful) {
                    resetCreateProductFormState()
                    onSuccess()
                } else {
                    onError("Error del servidor: ${resp.code()} - ${resp.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido al crear producto")
            }
        }
    }

    fun updateProduct(id: Int, updatedFormState: CreateProductFormState, storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val s = updatedFormState
        val request = ProductRequest(
            sku = s.sku,
            name = s.name,
            description = s.description,
            imageKey = _editImageKey.value ?: s.imageKey,   // 👈
            brand = s.brand,
            categoryId = s.categoryId,
            storeId = storeId,
            stock = s.stock.toIntOrNull() ?: 0,
            expirationDate = "No aplica"
        )
        viewModelScope.launch {
            try {
                val resp = productRepository.updateProduct(id, request)
                if (resp.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error actualizando: ${resp.code()} - ${resp.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido al actualizar")
            }
        }
    }

    fun deleteProduct(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = productRepository.deleteProduct(id)
                if (resp.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error eliminando: ${resp.code()} - ${resp.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido al eliminar")
            }
        }
    }

    fun updateCreateProductFormState(newState: CreateProductFormState) {
        _createProductFormState.value = newState
    }

    fun resetCreateProductFormState() {
        _createProductFormState.value = CreateProductFormState()
        _imageUploadUiState.value = UploadImageState.Idle
    }

    fun prepareFormForEdit(product: Product) {
        _createProductFormState.value = CreateProductFormState(
            name = product.name,
            description = product.description,
            sku = product.sku ?: "",
            brand = product.brand,
            categoryId = product.categoryId,
            imageUrl = product.imageUrl ?: product.image,
            stock = product.stock?.toString() ?: "0",
            expirationDate = product.expirationDate ?: ""
        )
        _imageUploadUiState.value = UploadImageState.Idle
        _editImageKey.value = null
    }

    fun handleProductImageSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "products", 0)) {
                is ImageUploadResult.Success -> {
                    _createProductFormState.update { it.copy(imageKey = r.imageKey, imageUrl = it.imageUrl) }
                    _imageUploadUiState.value = UploadImageState.Idle
                }
                is ImageUploadResult.Error -> {
                    _imageUploadUiState.value = UploadImageState.Error(r.message)
                }
            }
        }
    }

    fun selectImageForEdit(productId: Int, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "products", productId)) {
                is ImageUploadResult.Success -> {
                    _editImageKey.value = r.imageKey
                    _imageUploadUiState.value = UploadImageState.Idle
                }
                is ImageUploadResult.Error -> {
                    _imageUploadUiState.value = UploadImageState.Error(r.message)
                }
            }
        }
    }
    fun clearEditImageKey() { _editImageKey.value = null }
}
