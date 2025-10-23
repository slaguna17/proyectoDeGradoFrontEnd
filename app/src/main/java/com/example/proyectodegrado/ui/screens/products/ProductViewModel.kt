package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.*
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    private val _formState = MutableStateFlow(CreateProductFormState())
    val formState: StateFlow<CreateProductFormState> = _formState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    private val _stores = MutableStateFlow<List<StoreOption>>(emptyList())
    val stores: StateFlow<List<StoreOption>> = _stores.asStateFlow()

    private val _selectedStoreId = MutableStateFlow<Int?>(null)
    val selectedStoreId: StateFlow<Int?> = _selectedStoreId.asStateFlow()

    init {
        fetchAvailableCategories()
    }

    // --- Ayudantes para el formulario ---
    fun onNameChange(v: String) = _formState.update { it.copy(name = v) }
    fun onDescriptionChange(v: String) = _formState.update { it.copy(description = v) }
    fun onSkuChange(v: String) = _formState.update { it.copy(sku = v) }
    fun onBrandChange(v: String) = _formState.update { it.copy(brand = v) }
    fun onStockChange(v: String) = _formState.update { it.copy(stock = v) }
    fun onCategorySelected(id: Int) = _formState.update { it.copy(categoryId = id) }
    fun onImageSelected(uri: Uri?) = _formState.update { it.copy(localImageUri = uri) }

    fun resetForm() {
        _formState.value = CreateProductFormState()
        _imageUploadUiState.value = UploadImageState.Idle
    }

    fun fetchStores(onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val fetched = storeRepository.getAllStores()
                _stores.value = fetched.map { StoreOption(it.id, it.name) }
            } catch (e: Exception) { onError("Error cargando tiendas: ${e.message}") }
        }
    }

    fun setSelectedStore(storeId: Int?) {
        _selectedStoreId.value = storeId
    }

    fun fetchAllProducts(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            when (val result = productRepository.getAllProducts()) {
                is ApiResult.Success -> {
                    _products.value = result.data
                    onSuccess()
                }
                is ApiResult.Error -> onError("Error cargando productos: ${result.message}")
            }
        }
    }

    fun fetchProductsByCategory(
        categoryId: Int,
        storeId: Int?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            // --- CORRECCIÓN AQUÍ ---
            val result = if (storeId != null) {
                productRepository.getProductsByCategoryAndStore(categoryId, storeId)
            } else {
                productRepository.getProductsByCategory(categoryId)
            }

            when (result) {
                is ApiResult.Success -> {
                    _productsByCategory.value = result.data
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _productsByCategory.value = emptyList()
                    onError("Error cargando productos: ${result.message}")
                }
            }
        }
    }

    fun fetchProductsByStore(storeId: Int, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            when (val result = productRepository.getProductsByStore(storeId)) {
                is ApiResult.Success -> {
                    _products.value = result.data
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _products.value = emptyList()
                    onError("Error cargando productos de la tienda: ${result.message}")
                }
            }
        }
    }

    fun createProduct(storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            if (form.name.isBlank() || form.categoryId <= 0) {
                return@launch onError("Nombre y Categoría son requeridos.")
            }
            val initialRequest = ProductRequest(
                sku = form.sku.takeIf { it.isNotBlank() },
                name = form.name, description = form.description, imageKey = null, brand = form.brand,
                categoryId = form.categoryId, storeId = storeId, stock = form.stock.toIntOrNull() ?: 0,
            )

            // --- CORRECCIÓN AQUÍ ---
            when (val creationResult = productRepository.createProduct(initialRequest)) {
                is ApiResult.Success -> {
                    val newProduct = creationResult.data
                    // La lógica de subida de imagen ahora va dentro del caso de éxito
                    val uri = form.localImageUri
                    if (uri != null) {
                        _imageUploadUiState.value = UploadImageState.Uploading
                        val uploadResult = imageRepository.uploadImage(uri, "products", newProduct.id, "main")
                        if (uploadResult is ImageUploadResult.Success) {
                            val finalRequest = initialRequest.copy(imageKey = uploadResult.imageKey)
                            productRepository.updateProduct(newProduct.id, finalRequest) // No es necesario manejar el resultado aquí si es solo para actualizar la imagen
                        } else if (uploadResult is ImageUploadResult.Error) {
                            onError("Producto creado, pero falló la subida de imagen: ${uploadResult.message}")
                        }
                    }
                    _imageUploadUiState.value = UploadImageState.Idle
                    resetForm()
                    onSuccess()
                }
                is ApiResult.Error -> {
                    onError("Error creando producto: ${creationResult.message}")
                }
            }
        }
    }

    fun updateProduct(id: Int, storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            var finalImageKey: String? = form.imageKey

            val uri = form.localImageUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "products", id, "main")
                if (uploadResult is ImageUploadResult.Success) {
                    finalImageKey = uploadResult.imageKey
                } else if (uploadResult is ImageUploadResult.Error) {
                    _imageUploadUiState.value = UploadImageState.Error(uploadResult.message)
                    return@launch onError("Falló la subida de la nueva imagen: ${uploadResult.message}")
                }
            }

            val request = ProductRequest(
                sku = form.sku.takeIf { it.isNotBlank() },
                name = form.name, description = form.description, imageKey = finalImageKey, brand = form.brand,
                categoryId = form.categoryId, storeId = storeId, stock = form.stock.toIntOrNull() ?: 0,
            )

            // --- CORRECCIÓN AQUÍ ---
            // Asignamos la respuesta a la variable 'result'
            when (val result = productRepository.updateProduct(id, request)) {
                is ApiResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    resetForm()
                    onSuccess()
                }
                // Y ahora usamos 'result' en lugar de 'it'
                is ApiResult.Error -> onError("Error al actualizar el producto: ${result.message}")
            }
        }
    }

    fun deleteProduct(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = productRepository.deleteProduct(id)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> onError(result.message)
            }
        }
    }

    fun prepareFormForEdit(product: Product, currentStoreId: Int?) {
        val storeStock: Int? = currentStoreId?.let { sid ->
            product.stores?.firstOrNull { it.id == sid }?.pivot?.stock
        }
        _formState.value = CreateProductFormState(
            name = product.name,
            description = product.description,
            sku = product.sku ?: "",
            brand = product.brand,
            categoryId = product.categoryId,
            imageKey = product.image,
            imageUrl = product.imageUrl,
            stock = (storeStock ?: product.stock ?: 0).toString(),
            localImageUri = null
        )
        _imageUploadUiState.value = UploadImageState.Idle
    }

    fun fetchAvailableCategories() {
        viewModelScope.launch {
            try {
                _availableCategories.value = categoryRepository.getAllCategories()
            } catch (e: Exception) {
                println("Error fetching categories for ViewModel: ${e.message}")
            }
        }
    }

    fun addProductToStore(productId: Int, storeId: Int, stock: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = productRepository.addProductToStore(productId, storeId, stock)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> onError("Error al asignar el producto: ${result.message}")
            }
        }
    }

    fun removeProductFromStore(productId: Int, storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            // --- CORRECCIÓN AQUÍ ---
            when (val result = productRepository.removeProductFromStore(productId, storeId)) {
                is ApiResult.Success -> onSuccess()
                is ApiResult.Error -> onError("Error al quitar el producto: ${result.message}")
            }
        }
    }
}