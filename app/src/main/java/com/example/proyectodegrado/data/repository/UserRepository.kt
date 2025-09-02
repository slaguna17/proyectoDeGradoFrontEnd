package com.example.proyectodegrado.data.repository

import android.content.Context
import com.example.proyectodegrado.data.api.UserService
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.di.AppPreferences
import retrofit2.Response

class UserRepository(
    private val userService: UserService,
    private val context: Context
) {

    // -------------------- Auth --------------------

    /** Devuelve retrofit2.Response para usar response.isSuccessful / body() / code() */
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return userService.login(LoginRequest(email, password))
    }

    suspend fun forgotPassword(email: String): Response<ForgotPasswordResponse> {
        return userService.forgotPassword(ForgotPasswordRequest(email))
    }

    suspend fun resetPassword(token: String, newPassword: String): Response<ResetPasswordResponse> {
        return userService.resetPassword(ResetPasswordRequest(token, newPassword))
    }

    // -------------------- Users --------------------

    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return userService.registerUser(request)
    }

    suspend fun getAllUsers(): List<User> = userService.getAllUsers()

    suspend fun getUserById(userId: Int): User = userService.getUser(userId)

    suspend fun getRoles(): List<Role> = userService.getRoles()

    /** Conveniencia: trae el usuario usando el id guardado en preferencias (o null si no hay) */
    suspend fun getCurrentUser(): User? =
        getCurrentUserId()?.let { id -> userService.getUser(id) }

    /**
     * Actualiza el perfil del usuario actual. Puedes pasar avatarKey (KEY de S3)
     * o indicar removeImage=true para borrar el avatar en backend.
     * Devuelve true/false según HTTP 2xx.
     */
    suspend fun updateUserProfile(
        fullName: String,
        email: String,
        phone: String,
        avatarKey: String? = null,
        removeImage: Boolean = false
    ): Boolean {
        val userId = getCurrentUserId() ?: return false

        // NOTA: El servicio espera Map<String, Any?> (en la interfaz ya está con @JvmSuppressWildcards)
        val body = mutableMapOf<String, Any?>(
            "full_name" to fullName,
            "email" to email,
            "phone" to phone
        )
        if (avatarKey != null) body["avatar_key"] = avatarKey
        if (removeImage)      body["removeImage"] = true

        return try {
            userService.updateUser(userId, body).isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    // -------------------- Helpers --------------------

    private fun getCurrentUserId(): Int? =
        AppPreferences(context).getUserId()?.toIntOrNull()
}
