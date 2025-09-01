package com.example.proyectodegrado.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.ui.components.UploadImageState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.proyectodegrado.data.model.ImageUploadResult

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository  // <- nuevo
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _uploadState = MutableStateFlow<UploadImageState>(UploadImageState.Idle)
    val uploadState: StateFlow<UploadImageState> = _uploadState

    /** Key temporal cuando el usuario selecciona un nuevo avatar */
    private val _pendingAvatarKey = MutableStateFlow<String?>(null)
    val pendingAvatarKey: StateFlow<String?> = _pendingAvatarKey

    init { loadUser() }

    private fun loadUser() {
        viewModelScope.launch { _user.value = userRepository.getCurrentUser() }
    }

    fun handleAvatarSelection(uri: Uri?) {
        if (uri == null) return
        val id = _user.value?.id
        if (id == null) {
            _uploadState.value = UploadImageState.Error("No se puede cambiar el avatar sin un ID de usuario.")
            return
        }

        viewModelScope.launch {
            _uploadState.value = UploadImageState.Uploading
            // ✨ CAMBIO: Usar el método unificado 'uploadImage'.
            when (val result = imageRepository.uploadImage(uri, "users", id, "avatar")) {
                is ImageUploadResult.Success -> {
                    _pendingAvatarKey.value = result.imageKey
                    // Actualizamos el perfil inmediatamente con la nueva clave.
                    updateProfileWithNewAvatar(result.imageKey)
                }
                is ImageUploadResult.Error -> {
                    _uploadState.value = UploadImageState.Error(result.message)
                }
            }
        }
    }

    private fun updateProfileWithNewAvatar(avatarKey: String) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch
            val success = userRepository.updateUserProfile(
                fullName = currentUser.fullName,
                email = currentUser.email,
                phone = currentUser.phone,
                avatarKey = avatarKey,
                removeImage = false
            )
            if (success) {
                _pendingAvatarKey.value = null
                loadUser() // Recargar el usuario para mostrar la nueva imagen
            } else {
                _uploadState.value = UploadImageState.Error("No se pudo guardar el nuevo avatar.")
            }
            _uploadState.value = UploadImageState.Idle
        }
    }

    fun removeAvatar(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val ok = userRepository.updateUserProfile(
                fullName = _user.value?.fullName ?: "",
                email = _user.value?.email ?: "",
                phone = _user.value?.phone ?: "",
                avatarKey = null,
                removeImage = true
            )
            if (ok) { loadUser(); onSuccess() } else onError("No se pudo eliminar el avatar")
        }
    }

    fun updateProfile(fullName: String, email: String, phone: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val ok = userRepository.updateUserProfile(
                fullName = fullName,
                email = email,
                phone = phone,
                avatarKey = _pendingAvatarKey.value,
                removeImage = false
            )
            if (ok) {
                _pendingAvatarKey.value = null
                loadUser()
                onSuccess()
            } else onError("Error actualizando datos")
        }
    }
}
