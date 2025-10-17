package com.example.proyectodegrado.ui.screens.register

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ImageUploadResult
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.di.DependencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

// Estado simple de pantalla
sealed interface RegisterState {
    object Idle : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUi())
    val ui: StateFlow<RegisterUi> = _ui

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

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

    fun uploadAvatarIfNeeded() {
        val current = _ui.value
        val uri = current.avatarPreview ?: return
        if (current.uploading || current.avatarKey != null) return

        _ui.update { it.copy(uploading = true, error = null, successMsg = null) }
        viewModelScope.launch {
            when (val up = imageRepository.uploadImage(
                imageUri = uri,
                entityType = "users",
                entityId = 0,
                fileKind = "avatar"
            )) {
                is ImageUploadResult.Success ->
                    _ui.update { it.copy(uploading = false, avatarKey = up.imageKey) }
                is ImageUploadResult.Error ->
                    _ui.update { it.copy(uploading = false, error = up.message) }
            }
        }
    }

    private fun validate(): String? {
        val u = _ui.value
        if (u.fullName.isBlank()) return "El nombre es obligatorio."
        if (u.username.isBlank()) return "El usuario es obligatorio."
        if (u.email.isBlank() || !u.email.contains("@")) return "El correo es inválido."
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
                // 1) Registrar usuario
                val request = RegisterRequest(
                    username = u.username.trim(),
                    email = u.email.trim(),
                    password = u.password,
                    fullName = u.fullName.trim(),
                    dateOfBirth = u.dateOfBirth.ifBlank { "1900-01-01" },
                    phone = u.phone.trim(),
                    roleId = u.roleId!!,
                    avatarKey = u.avatarKey,
                    avatar = null
                )
                val registerResp = userRepository.registerUser(request)
                if (!registerResp.isSuccessful) {
                    val msg = registerResp.errorBody()?.string() ?: "No se pudo registrar el usuario"
                    throw IllegalStateException(msg)
                }

                // 2) Login para obtener isAdmin y menú
                val loginResponse = userRepository.login(u.email, u.password)
                val user = loginResponse.user
                val isAdmin = loginResponse.isAdmin        // <-- toma isAdmin del top-level
                val menu = loginResponse.menu              // <-- menú dinámico del backend
                val storeId = 1                            // usa tu lógica real si corresponde

                // 3) Guardar sesión (incluyendo el menú)
                DependencyProvider.saveCurrentSession(
                    userId = user.id,
                    storeId = storeId,
                    isAdmin = isAdmin,
                    userEmail = u.email,
                    userName = user.username?.ifBlank { user.full_name },
                    menu = menu
                )

                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Error desconocido") }
                _registerState.value = RegisterState.Error(e.message ?: "Error desconocido")
            } finally {
                _ui.update { it.copy(loading = false) }
            }
        }
    }

    fun loadRoles() {
        viewModelScope.launch {
            runCatching { userRepository.getRoles() }
                .onSuccess { _roles.value = it }
                .onFailure { _ui.update { state -> state.copy(error = it.message) } }
        }
    }
}
