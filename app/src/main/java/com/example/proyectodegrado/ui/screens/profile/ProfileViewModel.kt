package com.example.proyectodegrado.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = false,
    val error: String? = null,

    val fullName: String = "",
    val email: String = "",
    val phone: String = "",

    // URL remota que viene ya resuelta por el backend
    val avatarUrl: String? = null,

    // Preview local cuando el usuario elige una nueva imagen
    val avatarPreview: Uri? = null,

    // KEY devuelta por la subida a S3 (para enviar al backend en save())
    val avatarKey: String? = null,

    // Flag para indicar que el usuario quiere eliminar su avatar
    val removeImage: Boolean = false,

    // Para habilitar/deshabilitar el botón Guardar
    val hasChanges: Boolean = false
)

class ProfileViewModel(
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        loadMe()
    }

    // ---------- Cargar perfil ----------
    fun loadMe() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        try {
            // getCurrentUser() usa el ID de prefs y tu UserService con signed=true por defecto
            val user: User? = userRepo.getCurrentUser() // :contentReference[oaicite:4]{index=4} :contentReference[oaicite:5]{index=5}
            if (user != null) {
                // Preferimos avatarUrl; si viene null, usamos avatar (puede ya ser URL)
                val resolved = user.avatarUrl ?: user.avatar // :contentReference[oaicite:6]{index=6}
                _ui.update {
                    it.copy(
                        loading = false,
                        fullName = user.fullName,
                        email = user.email,
                        phone = user.phone,
                        avatarUrl = resolved,
                        avatarPreview = null,
                        avatarKey = null,
                        removeImage = false,
                        hasChanges = false
                    )
                }
            } else {
                _ui.update { it.copy(loading = false, error = "No se pudo obtener el usuario actual.") }
            }
        } catch (e: Exception) {
            _ui.update { it.copy(loading = false, error = e.message ?: "Error obteniendo el perfil") }
        }
    }

    // ---------- Handlers ----------
    fun onFullNameChange(v: String) = _ui.update { it.copy(fullName = v, hasChanges = true) }
    fun onPhoneChange(v: String) = _ui.update { it.copy(phone = v, hasChanges = true) }
    fun onEmailChange(v: String) = _ui.update { it.copy(email = v, hasChanges = true) }

    fun onPickAvatar(uri: Uri?) {
        if (uri == null) return
        // Preview inmediato y limpiamos estados previos
        _ui.update {
            it.copy(
                avatarPreview = uri,
                removeImage = false,
                avatarKey = null,
                hasChanges = true
            )
        }
    }

    fun onRemoveAvatar() {
        _ui.update {
            it.copy(
                avatarPreview = null,
                avatarKey = null,
                avatarUrl = null,
                removeImage = true,
                hasChanges = true
            )
        }
    }

    // ---------- Subir imagen a S3 (obtiene KEY) ----------
    private fun uploadAvatar(localUri: Uri) = viewModelScope.launch {
        when (val res = imageRepo.uploadImage(
            imageUri = localUri,
            entityType = "users",
            entityId = userRepo // usamos el helper del repo para el ID
                .run { // no expone publicamente getCurrentUserId(), así que sube a carpeta 0 o ajusta tu ImageRepository
                    // Si tu ImageRepository no requiere entityId real, puedes dejar 0
                    0
                },
            fileKind = "avatar"
        )) {
            is ImageUploadResult.Success -> {
                _ui.update { it.copy(avatarKey = res.imageKey, hasChanges = true) }
            }
            is ImageUploadResult.Error -> {
                _ui.update { it.copy(error = res.message) }
            }
        }
    }

    // ---------- Guardar ----------
    fun save(onDone: () -> Unit = {}, onError: (String) -> Unit = {}) = viewModelScope.launch {
        val st = _ui.value
        _ui.update { it.copy(loading = true, error = null) }

        try {
            // 1) Resolver qué avatarKey enviar
            val keyToSend: String? = when {
                st.removeImage -> null
                st.avatarKey != null -> st.avatarKey
                st.avatarPreview != null -> {
                    // Sube ahora (bloquea save hasta terminar)
                    when (val up = imageRepo.uploadImage(
                        imageUri = st.avatarPreview,
                        entityType = "users",
                        entityId = /* tu userId o 0 si no aplica */ 0,
                        fileKind = "avatar"
                    )) {
                        is ImageUploadResult.Success -> up.imageKey
                        is ImageUploadResult.Error -> {
                            _ui.update { it.copy(loading = false, error = up.message) }
                            onError(up.message)
                            return@launch
                        }
                    }
                }
                else -> null // no tocar avatar
            }

            // 2) Actualiza el perfil en backend
            val ok = userRepo.updateUserProfile(
                fullName = st.fullName,
                email = st.email,
                phone = st.phone,
                avatarKey = keyToSend,
                removeImage = st.removeImage
            )

            if (ok) {
                // 3) Recargar para traer la URL final (resuelta por backend)
                loadMe()
                onDone()
            } else {
                _ui.update { it.copy(loading = false, error = "No se pudo guardar") }
                onError("No se pudo guardar")
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Error guardando"
            _ui.update { it.copy(loading = false, error = msg) }
            onError(msg)
        }
    }

}
