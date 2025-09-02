package com.example.proyectodegrado.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.model.User
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.di.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepo: UserRepository,
    private val imageRepo: ImageRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val error: String? = null,
        val fullName: String = "",
        val email: String = "",
        val phone: String = "",
        // Para mostrar imagen en UI (http/https o content:// para preview local)
        val avatarUrl: String? = null,
        // KEY S3 pendiente de guardar
        val avatarKey: String? = null,
        // Para habilitar/deshabilitar el botón Guardar
        val hasChanges: Boolean = false
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init { loadMe() }

    // ---------- Carga perfil ----------
    fun loadMe() = viewModelScope.launch {
        _ui.update { it.copy(loading = true, error = null) }
        try {
            val user: User? = userRepo.getCurrentUser()
            if (user != null) {
                _ui.update {
                    it.copy(
                        loading = false,
                        fullName = user.fullName ?: "",
                        email = user.email ?: "",
                        phone = user.phone ?: "",
                        // Si backend devuelve una URL completa, se usa; si es key, mostramos placeholder
                        avatarUrl = user.avatar?.takeIf { url -> url.startsWith("http") },
                        avatarKey = null,
                        hasChanges = false
                    )
                }
            } else {
                _ui.update { it.copy(loading = false, error = "No se pudo cargar el usuario") }
            }
        } catch (e: Exception) {
            _ui.update { it.copy(loading = false, error = e.message ?: "Error al cargar perfil") }
        }
    }

    // ---------- Binding de campos ----------
    fun onFullNameChange(v: String) = _ui.update { it.copy(fullName = v, hasChanges = true) }
    fun onEmailChange(v: String)    = _ui.update { it.copy(email = v, hasChanges = true) }
    fun onPhoneChange(v: String)    = _ui.update { it.copy(phone = v, hasChanges = true) }

    // ---------- Elegir avatar (sube a S3 con presign PUT) ----------
    fun onPickAvatar(uri: Uri?) = viewModelScope.launch {
        if (uri == null) return@launch
        val uid = prefs.getUserId()?.toIntOrNull()
        if (uid == null) {
            _ui.update { it.copy(error = "No hay usuario en sesión") }
            return@launch
        }
        // Preview inmediato (Coil acepta content://)
        _ui.update { it.copy(avatarUrl = uri.toString(), loading = true, error = null) }

        when (val r = imageRepo.uploadImage(uri, entityType = "users", entityId = uid, fileKind = "avatar")) {
            is ImageUploadResult.Success ->
                _ui.update { it.copy(loading = false, avatarKey = r.imageKey, hasChanges = true) }
            is ImageUploadResult.Error   ->
                _ui.update { it.copy(loading = false, error = r.message) }
        }
    }

    // ---------- Quitar avatar ----------
    fun removeAvatar() = viewModelScope.launch {
        _ui.update { it.copy(avatarUrl = null, avatarKey = null, hasChanges = true) }
    }

    // ---------- Guardar ----------
    fun save(onDone: () -> Unit = {}) = viewModelScope.launch {
        val st = _ui.value
        _ui.update { it.copy(loading = true, error = null) }
        val ok = userRepo.updateUserProfile(
            fullName   = st.fullName,
            email      = st.email,
            phone      = st.phone,
            avatarKey  = st.avatarKey,        // si hay KEY nueva, el backend la guarda
            removeImage = (st.avatarUrl == null && st.avatarKey == null) // quitar avatar
        )
        if (ok) {
            // Refrescamos para que venga avatarUrl definitiva (si backend devuelve URL)
            loadMe()
            onDone()
        } else {
            _ui.update { it.copy(loading = false, error = "No se pudo guardar") }
        }
    }
}
