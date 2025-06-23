package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.ImageUploadResult
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _createCategoryFormState = MutableStateFlow(CreateCategoryFormState())
    val createCategoryFormState: StateFlow<CreateCategoryFormState> = _createCategoryFormState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    fun fetchCategories(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val list = categoryRepository.getAllCategories()
                _categories.value = list
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP: ${e.message}") }
            catch (e: Exception) { onError("Error: ${e.message}") }
        }
    }

    fun createCategoryFromState(onSuccess: () -> Unit = {}, onError: (String) -> Unit) {
        val s = _createCategoryFormState.value
        if (s.name.isBlank()) { onError("Nombre vacío"); return }
        val imageUrl = s.imageUrl ?: ""
        if (_imageUploadUiState.value !is UploadImageState.Idle) { onError("Subiendo imagen"); return }

        viewModelScope.launch {
            val response = categoryRepository.createCategory(CategoryRequest(s.name, s.description, imageUrl))
            if (response.isSuccessful) {
                _createCategoryFormState.value = CreateCategoryFormState()
                _imageUploadUiState.value = UploadImageState.Idle
                fetchCategories(onSuccess, onError)
            } else {
                onError("Error al crear categoría")
            }
        }
    }

    fun updateCategory(id: Int, request: CategoryRequest, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val resp = categoryRepository.updateCategory(id, request)
                if (resp.isSuccessful) fetchCategories(onSuccess, onError)
                else onError("Falló la actualización")
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteCategory(id: Int, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val resp = categoryRepository.deleteCategory(id)
                if (resp.isSuccessful) fetchCategories(onSuccess, onError)
                else onError("Falló la eliminación")
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateCreateCategoryFormState(newState: CreateCategoryFormState) {
        _createCategoryFormState.value = newState
    }

    fun handleCategoryImageSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading

            // TEMPORAL: usar una URL de imagen por defecto si no hay AWS
            val fakeImageUrl = "https://via.placeholder.com/300x300.png?text=Categoria"
            _imageUploadUiState.value = UploadImageState.Idle
            _createCategoryFormState.update { it.copy(imageUrl = fakeImageUrl) }

            // SI USAS AWS, DESCOMENTA ESTA LÓGICA:
            /*
            when (val result = imageRepository.getPresignedUrlAndUpload(uri, "category", 0)) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _createCategoryFormState.update { it.copy(imageUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(result.message)
            }
            */
        }
    }
}
