package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.CreateCategoryFormState
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
            catch (e: Exception) { onError("Desconocido: ${e.message}") }
        }
    }

    fun createCategoryFromState(onSuccess: () -> Unit = {}, onError: (String) -> Unit) {
        val s = _createCategoryFormState.value
        if (s.name.isBlank()) { onError("Nombre vacío"); return }
        if (s.imageUrl == null) { onError("Imagen requerida"); return }
        if (_imageUploadUiState.value !is UploadImageState.Idle) { onError("Subiendo imagen"); return }
        viewModelScope.launch {
            when (val resp = categoryRepository.createCategory(CategoryRequest(s.name, s.description, s.imageUrl))) {
                is retrofit2.Response<*> -> if (resp.isSuccessful) {
                    _createCategoryFormState.value = CreateCategoryFormState()
                    _imageUploadUiState.value = UploadImageState.Idle
                    fetchCategories(onSuccess = onSuccess, onError = onError)
                } else onError("Error servidor")
            }
        }
    }

    fun updateCategory(
        id: Int,
        request: CategoryRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val resp = categoryRepository.updateCategory(id, request)
                if (resp.isSuccessful) {
                    fetchCategories(onSuccess = onSuccess, onError = onError)
                } else onError("Falló")
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun deleteCategory(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val resp = categoryRepository.deleteCategory(id)
                if (resp.isSuccessful) {
                    fetchCategories(onSuccess = onSuccess, onError = onError)
                } else onError("Falló")
            } catch (e: Exception) { onError(e.message ?: "Error") }
        }
    }

    fun updateCreateCategoryFormState(newState: CreateCategoryFormState) {
        _createCategoryFormState.value = newState
    }

    fun handleCategoryImageSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val result = imageRepository.getPresignedUrlAndUpload(uri, "category", 0)) {
                is ImageUploadResult.Success -> {
                    _imageUploadUiState.value = UploadImageState.Idle
                    _createCategoryFormState.update { it.copy(imageUrl = result.accessUrl) }
                }
                is ImageUploadResult.Error -> _imageUploadUiState.value = UploadImageState.Error(result.message)
            }
        }
    }
}
