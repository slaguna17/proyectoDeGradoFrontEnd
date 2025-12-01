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

    val avatarUrl: String? = null,
    val avatarPreview: Uri? = null,
    val avatarKey: String? = null,
    val removeImage: Boolean = false,
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

    // ---------- Load Profile ----------
    fun loadMe() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        try {
            val user: User? = userRepo.getCurrentUser()
            if (user != null) {
                val resolved = user.avatarUrl ?: user.avatar
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

    private fun uploadAvatar(localUri: Uri) = viewModelScope.launch {
        when (val res = imageRepo.uploadImage(
            imageUri = localUri,
            entityType = "users",
            entityId = userRepo
                .run { 0 },
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

    // ---------- Save ----------
    fun save(onDone: () -> Unit = {}, onError: (String) -> Unit = {}) = viewModelScope.launch {
        val st = _ui.value
        _ui.update { it.copy(loading = true, error = null) }

        try {
            val keyToSend: String? = when {
                st.removeImage -> null
                st.avatarKey != null -> st.avatarKey
                st.avatarPreview != null -> {
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
                else -> null
            }

            val ok = userRepo.updateUserProfile(
                fullName = st.fullName,
                email = st.email,
                phone = st.phone,
                avatarKey = keyToSend,
                removeImage = st.removeImage
            )

            if (ok) {
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
