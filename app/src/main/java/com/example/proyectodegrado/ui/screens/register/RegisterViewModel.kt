package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.LoginResponse
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class RegisterUi(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "", // yyyy-MM-dd
    val roleId: Int? = null,

    val password: String = "",
    val confirmPassword: String = "",

    val avatarPreview: Uri? = null,
    val avatarKey: String? = null, // KEY upload to S3

    val loading: Boolean = false,
    val uploading: Boolean = false,
    val error: String? = null,
    val successMsg: String? = null
)

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUi())
    val ui: StateFlow<RegisterUi> = _ui

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles

    private val _authState = MutableLiveData<Result<LoginResponse>>()
    val authState: LiveData<Result<LoginResponse>> = _authState

    /** ───── Helpers ───── */
    fun onFullName(v: String) = _ui.update { it.copy(fullName = v, error = null, successMsg = null) }
    fun onUsername(v: String) = _ui.update { it.copy(username = v, error = null, successMsg = null) }
    fun onEmail(v: String) = _ui.update { it.copy(email = v, error = null, successMsg = null) }
    fun onPhone(v: String) = _ui.update { it.copy(phone = v, error = null, successMsg = null) }
    fun onRoleId(v: Int?) = _ui.update { it.copy(roleId = v, error = null, successMsg = null) }
    fun onPassword(v: String) = _ui.update { it.copy(password = v, error = null, successMsg = null) }
    fun onConfirmPassword(v: String) = _ui.update { it.copy(confirmPassword = v, error = null, successMsg = null) }

    fun onDatePicked(millis: Long?) {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val str = if (millis != null) fmt.format(Date(millis)) else ""
        _ui.update { it.copy(dateOfBirth = str, error = null, successMsg = null) }
    }

    fun onPickAvatar(uri: Uri?) {
        _ui.update { it.copy(avatarPreview = uri, avatarKey = null, error = null, successMsg = null) }
    }

    /** Sube avatar opcional a S3 y guarda su KEY. */
    fun uploadAvatarIfNeeded() {
        val current = _ui.value
        val uri = current.avatarPreview ?: return
        if (current.uploading) return

        _ui.update { it.copy(uploading = true, error = null, successMsg = null) }
        viewModelScope.launch {
            when (val up = imageRepository.uploadImage(
                imageUri = uri,
                entityType = "users",
                entityId = 0,           //if users does not exist yet -> folder 0
                fileKind = "avatar"
            )) {
                is ImageUploadResult.Success ->
                    _ui.update { it.copy(uploading = false, avatarKey = up.imageKey) }
                is ImageUploadResult.Error ->
                    _ui.update { it.copy(uploading = false, error = up.message) }
            }
        }
    }

    /** Validación simple */
    private fun validate(): String? {
        val u = _ui.value
        if (u.fullName.isBlank()) return "El nombre es obligatorio."
        if (u.username.isBlank()) return "El usuario es obligatorio."
        if (u.email.isBlank()) return "El correo es obligatorio."
        if (u.roleId == null) return "Selecciona un rol."
        if (u.password.length < 6) return "La contraseña debe tener al menos 6 caracteres."
        if (u.password != u.confirmPassword) return "Las contraseñas no coinciden."
        return null
    }

    fun register() {
        val error = validate()
        if (error != null) {
            _ui.update { it.copy(error = error) }
            return
        }
        val u = _ui.value
        _ui.update { it.copy(loading = true, error = null, successMsg = null) }

        viewModelScope.launch {
            try {
                val req = RegisterRequest(
                    username = u.username,
                    email = u.email,
                    password = u.password,
                    fullName = u.fullName,
                    dateOfBirth = u.dateOfBirth.ifBlank { "1900-01-01" },
                    phone = u.phone,
                    roleId = u.roleId!!,
                    avatarKey = u.avatarKey, //S3 Key if uploaded
                    avatar = null
                )
                val resp = userRepository.registerUser(req)
                if (resp.isSuccessful) {
                    // Registro OK → inicia sesión con las mismas credenciales
                    val loginResp = userRepository.login(u.email, u.password)
                    if (loginResp.isSuccessful && loginResp.body() != null) {
                        _authState.postValue(Result.success(loginResp.body()!!))
                        _ui.update { it.copy(loading = false, successMsg = "Usuario creado e iniciado sesión.", error = null) }
                    } else {
                        _ui.update {
                            it.copy(
                                loading = false,
                                error = "Registrado, pero no se pudo iniciar sesión (HTTP ${loginResp.code()})"
                            )
                        }
                    }
                } else {
                    _ui.update { it.copy(loading = false, error = "No se pudo registrar. Código ${resp.code()}") }
                }
            } catch (e: Exception) {
                _ui.update { it.copy(loading = false, error = e.message ?: "Error desconocido") }
            }
        }
    }

    fun loadRoles() {
        viewModelScope.launch {
            runCatching { userRepository.getRoles() }
                .onSuccess { _roles.value = it }
                .onFailure { /* podrías setear un error si quieres */ }
        }
    }

    private inline fun <T> MutableStateFlow<T>.update(block: (T) -> T) { value = block(value) }
}
