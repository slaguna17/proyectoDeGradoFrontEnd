package com.example.proyectodegrado.ui.screens.categories

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _formState = MutableStateFlow(CreateCategoryFormState())
    val formState: StateFlow<CreateCategoryFormState> = _formState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    fun fetchCategories(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepository.getAllCategories()
                onSuccess()
            } catch (e: Exception) {
                onError("Error cargando categorías: ${e.message}")
            }
        }
    }

    // --- Ayudantes del formulario ---
    fun onNameChange(name: String) = _formState.update { it.copy(name = name) }
    fun onDescriptionChange(desc: String) = _formState.update { it.copy(description = desc) }
    fun onImageSelected(uri: Uri?) = _formState.update { it.copy(localImageUri = uri) }

    fun resetForm() {
        _formState.value = CreateCategoryFormState()
        _imageUploadUiState.value = UploadImageState.Idle
    }

    fun prepareFormForEdit(category: Category) {
        _formState.value = CreateCategoryFormState(
            name = category.name,
            description = category.description ?: "",
            imageKey = category.image,
            imageUrl = category.imageUrl
        )
    }

    // --- Lógica Principal ---
    fun createCategory(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            if (form.name.isBlank()) return@launch onError("El nombre es obligatorio.")

            val response = try {
                categoryRepository.createCategory(form.name, form.description, null)
            } catch (e: Exception) {
                return@launch onError("Error de red creando categoría: ${e.message}")
            }
            val newCategory = response.body() ?: return@launch onError("El servidor no pudo crear la categoría.")

            val uri = form.localImageUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "categories", newCategory.id, "icon")
                if (uploadResult is ImageUploadResult.Success) {
                    val finalRequest = CategoryRequest(form.name, form.description, uploadResult.imageKey)
                    categoryRepository.updateCategory(newCategory.id, finalRequest)
                } else if (uploadResult is ImageUploadResult.Error) {
                    onError("Categoría creada, pero falló la subida de imagen: ${uploadResult.message}")
                }
            }
            _imageUploadUiState.value = UploadImageState.Idle
            resetForm()
            fetchCategories(onSuccess)
        }
    }

    fun updateCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            var finalImageKey: String? = form.imageKey

            val uri = form.localImageUri
            if (uri != null) {
                _imageUploadUiState.value = UploadImageState.Uploading
                val uploadResult = imageRepository.uploadImage(uri, "categories", id, "icon")
                if (uploadResult is ImageUploadResult.Success) {
                    finalImageKey = uploadResult.imageKey
                } else if (uploadResult is ImageUploadResult.Error) {
                    _imageUploadUiState.value = UploadImageState.Error(uploadResult.message)
                    return@launch onError("Falló la subida de la nueva imagen: ${uploadResult.message}")
                }
            }

            val request = CategoryRequest(form.name, form.description, finalImageKey)
            try {
                categoryRepository.updateCategory(id, request)
                _imageUploadUiState.value = UploadImageState.Idle
                resetForm()
                fetchCategories(onSuccess)
            } catch (e: Exception) {
                onError("Error al actualizar la categoría: ${e.message}")
            }
        }
    }

    fun deleteCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(id)
                fetchCategories(onSuccess)
            } catch (e: Exception) {
                onError("Error al eliminar categoría: ${e.message}")
            }
        }
    }
}