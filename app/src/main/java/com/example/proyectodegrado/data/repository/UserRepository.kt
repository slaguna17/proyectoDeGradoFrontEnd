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
    suspend fun login(email: String, password: String): Response<LoginResponse> =
        userService.login(LoginRequest(email, password))

    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> =
        userService.registerUser(request)

    suspend fun getAllUsers(): List<User> = userService.getAllUsers()

    suspend fun getUserById(userId: Int): User = userService.getUser(userId)

    suspend fun getRoles(): List<Role> = userService.getRoles()

    suspend fun forgotPassword(email: String): Response<ForgotPasswordResponse> =
        userService.forgotPassword(ForgotPasswordRequest(email))

    suspend fun resetPassword(token: String, newPassword: String): Response<ResetPasswordResponse> =
        userService.resetPassword(ResetPasswordRequest(token, newPassword))

    suspend fun getCurrentUser(): User? = getCurrentUserId()?.let { userService.getUser(it) }

    /** Actualiza perfil; opcionalmente cambia avatar con avatarKey o lo elimina con removeImage */
    suspend fun updateUserProfile(
        fullName: String,
        email: String,
        phone: String,
        avatarKey: String? = null,
        removeImage: Boolean = false
    ): Boolean {
        val userId = getCurrentUserId() ?: return false
        val req = mutableMapOf<String, Any?>(
            "full_name" to fullName,
            "email" to email,
            "phone" to phone
        )
        if (avatarKey != null) req["avatar_key"] = avatarKey
        if (removeImage) req["removeImage"] = true
        return try { userService.updateUser(userId, req).isSuccessful } catch (_: Exception) { false }
    }

    private fun getCurrentUserId(): Int? = AppPreferences(context).getUserId()?.toIntOrNull()
}
