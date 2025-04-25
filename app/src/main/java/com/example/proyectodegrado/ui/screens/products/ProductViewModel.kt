// app/src/main/java/com/example/proyectodegrado/ui/screens/products/ProductViewModel.kt
package com.example.proyectodegrado.ui.screens.products

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.* // Tus modelos y DTOs
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository // Importa el repo de imagen
import com.example.proyectodegrado.data.repository.ImageUploadResult // Importa el resultado sellado
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.ui.components.UploadImageState // Importa el estado de UI
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    // --- Estados para Listas y Mensajes ---
    private var categoryResult: String = ""
    private var productResult: String = ""

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList()) // Para todos los productos si es necesario
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _productsByCategory = MutableStateFlow<List<Product>>(emptyList())
    val productsByCategory: StateFlow<List<Product>> = _productsByCategory.asStateFlow()

    // --- Estado para el Formulario de Creación de Producto ---
    private val _createProductFormState = MutableStateFlow(CreateProductFormState())
    val createProductFormState: StateFlow<CreateProductFormState> = _createProductFormState.asStateFlow()

    // --- Estado para el Formulario de Creación de Categoría ---
    private val _createCategoryFormState = MutableStateFlow(CreateCategoryFormState())
    val createCategoryFormState: StateFlow<CreateCategoryFormState> = _createCategoryFormState.asStateFlow()

    // --- Estado para la Subida de Imágenes (Compartido) ---
    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    // --- Funciones para manejar selección/subida de imágenes ---
    fun handleProductImageSelection(uri: Uri?) {
        if (uri == null) { Log.d("ProductViewModel", "Selección de imagen de producto cancelada."); return }
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            val result = imageRepository.getPresignedUrlAndUpload(uri, "product", 0) // ID temporal
            when (result) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _createProductFormState.update { it.copy(imageUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(result.message)
            }
        }
    }

    fun handleCategoryImageSelection(uri: Uri?) {
        if (uri == null) { Log.d("ProductViewModel", "Selección de imagen de categoría cancelada."); return }
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            val result = imageRepository.getPresignedUrlAndUpload(uri, "category", 0) // ID temporal
            when (result) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _createCategoryFormState.update { it.copy(imageUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(result.message)
            }
        }
    }

    // --- Funciones CRUD para Categorías ---

    // ****** FUNCIÓN fetchCategories RESTAURADA ******
    fun fetchCategories(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d("ProductViewModel", "Fetching categories...")
                val categoryList = categoryRepository.getAllCategories()
                _categories.value = categoryList
                Log.d("ProductViewModel", "Fetched ${categoryList.size} categories.")
                onSuccess()
            } catch (e: IOException) { Log.e("PVMCategories", "Network Error: ${e.message}"); onError(e.message ?: "Error de red") }
            catch (e: HttpException) { Log.e("PVMCategories", "HTTP Error: ${e.code()}"); onError(e.message ?: "Error HTTP") }
            catch (e: Exception) { Log.e("PVMCategories", "Unknown Error: ${e.message}", e); onError(e.message ?: "Error desconocido") }
        }
    }
    // *******************************************

    fun createCategoryFromState(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _createCategoryFormState.value
        // --- Validaciones ---
        if (currentState.name.isBlank()) { onError("El nombre no puede estar vacío."); return }
        if (currentState.imageUrl == null) { onError("Por favor, sube una imagen para la categoría."); return }
        if (imageUploadUiState.value !is UploadImageState.Idle) { onError("Espera a que termine la subida."); return }

        val request = CategoryRequest(currentState.name, currentState.description, currentState.imageUrl)
        viewModelScope.launch {
            try {
                val response = categoryRepository.createCategory(request)
                if (response.isSuccessful) {
                    _createCategoryFormState.value = CreateCategoryFormState() // Limpia formulario
                    _imageUploadUiState.value = UploadImageState.Idle // Resetea estado imagen
                    fetchCategories() // Refresca la lista (usa valores por defecto para onSuccess/onError)
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun updateCategory(id:Int, request: CategoryRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Añade la lógica para subir nueva imagen si es necesario, similar a handleCategoryImageSelection
        // y actualiza request.image con la nueva URL antes de llamar al repositorio
        viewModelScope.launch {
            try {
                val response = categoryRepository.updateCategory(id, request)
                if (response.isSuccessful) {
                    fetchCategories()
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun deleteCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.deleteCategory(id)
                if (response.isSuccessful) {
                    fetchCategories()
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    // --- Funciones CRUD para Productos ---

    fun fetchProducts(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d("ProductViewModel", "Fetching all products...")
                val productList = productRepository.getAllProducts()
                _products.value = productList
                Log.d("ProductViewModel", "Fetched ${productList.size} total products.")
                onSuccess()
            } catch (e: IOException) { Log.e("PVMProducts", "Network Error: ${e.message}"); onError(e.message ?: "Error de red") }
            catch (e: HttpException) { Log.e("PVMProducts", "HTTP Error: ${e.code()}"); onError(e.message ?: "Error HTTP") }
            catch (e: Exception) { Log.e("PVMProducts", "Unknown Error: ${e.message}", e); onError(e.message ?: "Error desconocido") }
        }
    }

    fun fetchProductsByCategory(categoryId: Int, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                Log.d("ProductViewModel", "Fetching products for category ID: $categoryId")
                val productList = categoryRepository.getProductsForCategory(categoryId)
                _productsByCategory.value = productList
                Log.d("ProductViewModel", "Fetched ${productList.size} products for category $categoryId")
                onSuccess()
            } catch (e: IOException) { Log.e("PVMProducts", "Network Error: ${e.message}"); onError(e.message ?: "Error de red") }
            catch (e: HttpException) { Log.e("PVMProducts", "HTTP Error: ${e.code()}"); onError(e.message ?: "Error HTTP") }
            catch (e: Exception) { Log.e("PVMProducts", "Unknown Error: ${e.message}", e); onError(e.message ?: "Error desconocido") }
        }
    }

    fun createProductFromState(categoryId: Int, storeId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentState = _createProductFormState.value
        val stockInt = currentState.stock.toIntOrNull() ?: 0
        // --- Validaciones ---
        if (currentState.name.isBlank()) { onError("Nombre vacío."); return }
        if (currentState.imageUrl == null) { onError("Imagen requerida."); return }
        if (imageUploadUiState.value !is UploadImageState.Idle) { onError("Espera subida imagen."); return }
        if (categoryId <= 0) { onError("Categoría inválida."); return }
        // ...
        val productData = ProductData( currentState.sku, currentState.name, currentState.description, currentState.imageUrl, categoryId,currentState.brand )
        val storeData = StoreData( storeId, stockInt, currentState.expirationDate )
        val request = ProductRequest(productData, storeData)
        viewModelScope.launch {
            try {
                val response = productRepository.createProduct(request.product, request.store)
                if (response.isSuccessful) {
                    _createProductFormState.value = CreateProductFormState() // Limpia
                    _imageUploadUiState.value = UploadImageState.Idle // Resetea
                    fetchProductsByCategory(categoryId) // Refresca
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun updateProduct(id:Int, request: ProductRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // TODO: Implementar lógica completa, incluyendo posible subida de nueva imagen
        // Si subes imagen, llama a handleProductImageSelection, obtén la URL,
        // actualiza request.product.image y LUEGO llama a productRepository.updateProduct
        viewModelScope.launch {
            try {
                val response = productRepository.updateProduct(id, request) // Asume que el request ya tiene la URL correcta
                if (response.isSuccessful) {
                    fetchProductsByCategory(request.product.category_id) // Refresca
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun deleteProduct(id: Int, categoryId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.deleteProduct(id)
                if (response.isSuccessful) {
                    fetchProductsByCategory(categoryId) // Refresca
                    onSuccess()
                } else { onError("Falló: ${response.errorBody()?.string() ?: response.message()}") }
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    // --- Funciones para actualizar estados de formularios ---

    // ESTA ES LA FUNCIÓN QUE NECESITAS PARA EL FORMULARIO DE PRODUCTO
    fun updateCreateProductFormState(newState: CreateProductFormState) {
        _createProductFormState.value = newState
    }

    // Y esta es la que creamos para el formulario de CATEGORÍA
    fun updateCreateCategoryFormState(newState: CreateCategoryFormState) {
        _createCategoryFormState.value = newState
    }

}