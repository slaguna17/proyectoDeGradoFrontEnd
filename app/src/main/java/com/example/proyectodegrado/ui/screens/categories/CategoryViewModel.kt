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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    /** Key temporal cuando en EDITAR el usuario elige una nueva imagen */
    private val _editImageKey = MutableStateFlow<String?>(null)
    val editImageKey: StateFlow<String?> = _editImageKey.asStateFlow()

    // ---------- LISTA ----------
    fun fetchCategories(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepository.getAllCategories()
                onSuccess()
            } catch (e: IOException) { onError("Red: ${e.message}") }
            catch (e: HttpException) { onError("HTTP ${e.code()}") }
            catch (e: Exception) { onError("Error: ${e.message}") }
        }
    }

    // ---------- CREAR ----------
    fun updateCreateCategoryFormState(newState: CreateCategoryFormState) {
        _createCategoryFormState.value = newState
    }

    fun resetCreateCategoryForm() {
        _createCategoryFormState.value = CreateCategoryFormState()
        _imageUploadUiState.value = UploadImageState.Idle
    }

    /** Subida (crear): entityId=0 */
    fun handleCategoryImageSelection(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "categories", 0)) {
                is ImageUploadResult.Success -> {
                    _createCategoryFormState.update { it.copy(imageKey = r.imageKey) }
                    _imageUploadUiState.value = UploadImageState.Idle
                }
                is ImageUploadResult.Error -> {
                    _imageUploadUiState.value = UploadImageState.Error(r.message)
                }
            }
        }
    }

    fun createCategoryFromState(onSuccess: () -> Unit = {}, onError: (String) -> Unit) {
        val s = _createCategoryFormState.value
        if (s.name.isBlank()) { onError("El nombre es obligatorio"); return }
        if (imageUploadUiState.value is UploadImageState.Loading) { onError("Esperando subida de imagen"); return }

        viewModelScope.launch {
            try {
                val resp = categoryRepository.createCategory(
                    name = s.name.trim(),
                    description = s.description.trim(),
                    imageKey = s.imageKey
                )
                if (resp.isSuccessful) {
                    resetCreateCategoryForm()
                    fetchCategories(onSuccess, onError)
                } else {
                    onError(resp.errorBody()?.string() ?: "Error al crear categoría")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    // ---------- EDITAR ----------
    /** Subida (editar): usa el id real de la categoría */
    fun selectImageForEdit(categoryId: Int, uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            _imageUploadUiState.value = UploadImageState.Loading
            when (val r = imageRepository.uploadWithPresignPut(uri, "categories", categoryId)) {
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
                    clearEditImageKey()
                    fetchCategories(onSuccess, onError)
                } else {
                    onError(resp.errorBody()?.string() ?: "Falló la actualización")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            }
        }
    }

    // ---------- ELIMINAR ----------
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
}
