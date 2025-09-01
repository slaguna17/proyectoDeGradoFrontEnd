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
            try {
                _products.value = productRepository.getAllProducts()
                onSuccess()
            } catch (e: Exception) { onError("Error cargando productos: ${e.message}") }
        }
    }

    fun fetchProductsByCategory(categoryId: Int, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _productsByCategory.value = productRepository.getProductsByCategory(categoryId)
                onSuccess()
            } catch (e: Exception) { onError("Error cargando productos por categoría: ${e.message}") }
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
            val response = try { productRepository.createProduct(initialRequest) }
            catch (e: Exception) { return@launch onError("Error de red creando producto: ${e.message}") }

            val newProduct = response.body() ?: return@launch onError("El servidor no pudo crear el producto.")

            val uri = form.localImageUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "products", newProduct.id, "main")
                if (uploadResult is ImageUploadResult.Success) {
                    val finalRequest = initialRequest.copy(imageKey = uploadResult.imageKey)
                    productRepository.updateProduct(newProduct.id, finalRequest)
                } else if (uploadResult is ImageUploadResult.Error) {
                    onError("Producto creado, pero falló la subida de imagen: ${uploadResult.message}")
                }
            }
            _imageUploadUiState.value = UploadImageState.Idle
            resetForm()
            onSuccess()
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
            try {
                productRepository.updateProduct(id, request)
                _imageUploadUiState.value = UploadImageState.Idle
                resetForm()
                onSuccess()
            } catch (e: Exception) {
                onError("Error al actualizar el producto: ${e.message}")
            }
        }
    }

    fun deleteProduct(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido al eliminar")
            }
        }
    }

    fun prepareFormForEdit(product: Product) {
        _formState.value = CreateProductFormState(
            name = product.name,
            description = product.description,
            sku = product.sku ?: "",
            brand = product.brand,
            categoryId = product.categoryId,
            imageKey = product.image,
            imageUrl = product.imageUrl,
            stock = product.stock?.toString() ?: "0",
            localImageUri = null
        )
        _imageUploadUiState.value = UploadImageState.Idle
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
}