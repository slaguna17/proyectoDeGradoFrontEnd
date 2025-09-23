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

    // 🔹 Nuevo: estado de carga para el pull-to-refresh
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _formState = MutableStateFlow(CreateCategoryFormState())
    val formState: StateFlow<CreateCategoryFormState> = _formState.asStateFlow()

    private val _imageUploadUiState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val imageUploadUiState: StateFlow<UploadImageState> = _imageUploadUiState.asStateFlow()

    fun fetchCategories(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _categories.value = categoryRepository.getAllCategories()
                onSuccess()
            } catch (e: Exception) {
                onError("Error cargando categorías: ${e.message}")
            } finally {
                _loading.value = false
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

            _loading.value = true
            try {
                val response = categoryRepository.createCategory(form.name, form.description, null)
                val newCategory = response.body() ?: return@launch onError("El servidor no pudo crear la categoría.")

                form.localImageUri?.let { uri ->
                    _imageUploadUiState.value = UploadImageState.Uploading
                    when (val upload = imageRepository.uploadImage(uri, "categories", newCategory.id, "icon")) {
                        is ImageUploadResult.Success -> {
                            val req = CategoryRequest(form.name, form.description, upload.imageKey)
                            categoryRepository.updateCategory(newCategory.id, req)
                        }
                        is ImageUploadResult.Error -> onError("Categoría creada, pero falló la subida de imagen: ${upload.message}")
                    }
                    _imageUploadUiState.value = UploadImageState.Idle
                }

                resetForm()
                fetchCategories(onSuccess)
            } catch (e: Exception) {
                onError("Error de red creando categoría: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val form = _formState.value
            var finalImageKey: String? = form.imageKey

            try {
                form.localImageUri?.let { uri ->
                    _imageUploadUiState.value = UploadImageState.Uploading
                    when (val upload = imageRepository.uploadImage(uri, "categories", id, "icon")) {
                        is ImageUploadResult.Success -> finalImageKey = upload.imageKey
                        is ImageUploadResult.Error -> {
                            _imageUploadUiState.value = UploadImageState.Error(upload.message)
                            return@launch onError("Falló la subida de la nueva imagen: ${upload.message}")
                        }
                    }
                }

                val request = CategoryRequest(form.name, form.description, finalImageKey)
                _loading.value = true
                categoryRepository.updateCategory(id, request)
                _imageUploadUiState.value = UploadImageState.Idle
                resetForm()
                fetchCategories(onSuccess)
            } catch (e: Exception) {
                onError("Error al actualizar la categoría: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteCategory(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                categoryRepository.deleteCategory(id)
                fetchCategories(onSuccess)
            } catch (e: Exception) {
                onError("Error al eliminar categoría: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}
