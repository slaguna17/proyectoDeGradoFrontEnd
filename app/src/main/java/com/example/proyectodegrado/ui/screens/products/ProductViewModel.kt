package com.example.proyectodegrado.ui.screens.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductData
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.StoreData
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    //Result Messages
    private var categoryResult: String = ""
    private var productResult: String = ""

    //List and state flows
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    var categories: StateFlow<List<Category>> = _categories

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productsByCategory = MutableStateFlow<List<Product>>(emptyList())
    val productsByCategory: StateFlow<List<Product>> = _productsByCategory

    //Single object flow
    private val emptyCategory = Category(-1, "","", "")
    private val _category = MutableStateFlow<Category>(emptyCategory)
    private val emptyProduct = Product(id = -1, "0", "","","","",0)
    private val _product = MutableStateFlow<Product>(emptyProduct)

    //Category Functions
    fun fetchCategories(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val categoryList = categoryRepository.getAllCategories()
                _categories.value = categoryList
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
//                e.printStackTrace()
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun fetchCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val category = categoryRepository.getCategory(id)
                _category.value = category
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
//                e.printStackTrace()
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun createCategory(request: CategoryRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.createCategory(request)
                if (response.isSuccessful) {
                    categoryResult = response.body()?.message ?: "Created Category successful!"
                    fetchCategories(onSuccess = onSuccess, onError = onError)
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

    fun updateCategory(id:Int, request: CategoryRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.updateCategory(id,request)
                if (response.isSuccessful) {
                    categoryResult = response.body()?.message ?: "Updated Category successfully!"
                    fetchCategories(onSuccess = onSuccess, onError = onError)
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

    fun deleteCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = categoryRepository.deleteCategory(id)
                if (response.isSuccessful) {
                    categoryResult = response.body()?.message ?: "Deleted category successfully!"
                    fetchCategories(onSuccess = onSuccess, onError = onError)
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
    //Product Functions
    fun fetchProducts(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val productList = productRepository.getAllProducts()
                _products.value = productList
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
//                e.printStackTrace()
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun fetchProduct(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val product = productRepository.getProduct(id)
                _product.value = product
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
//                e.printStackTrace()
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun createProduct(request: ProductRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.createProduct(request.product, request.store)
                if (response.isSuccessful) {
                    productResult = response.body()?.message ?: "Created Product successfully!"
                    onSuccess()
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

    fun updateProduct(id:Int, request: ProductRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.updateProduct(id,request)
                if (response.isSuccessful) {
                    productResult = response.body()?.message ?: "Updated Product successfully!"
                    fetchProductsByCategory(categoryId = id, onSuccess = onSuccess, onError = onError)
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

    fun deleteProduct(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = productRepository.deleteProduct(id)
                if (response.isSuccessful) {
                    productResult = response.body()?.message ?: "Deleted successfully!"
                    fetchProductsByCategory(categoryId = id, onSuccess = onSuccess, onError = onError)
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

    fun fetchProductsByCategory(categoryId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val productList = categoryRepository.getProductsForCategory(categoryId)
                _productsByCategory.value = productList
                onSuccess()
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }
}