package com.example.proyectodegrado.data.repository

import android.content.Context
import com.example.proyectodegrado.data.api.UserService
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider
import retrofit2.Response

class UserRepository(
    private val userService: UserService,
    private val context: Context
) {

    // -------------------- Auth --------------------

    suspend fun login(username: String, password: String): LoginResponse {
        val resp = userService.login(LoginRequest(username, password))

        AppPreferences(context).saveUserId(resp.user.id.toString())
        return resp
    }

    suspend fun me(bearer: String): MeResponse {
        return userService.me(bearer)
    }

    suspend fun menu(bearer: String): MenuResponse {
        return userService.menu(bearer)
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

    suspend fun getCurrentUser(): User? =
        getCurrentUserId()?.let { id -> userService.getUser(id) }

    suspend fun updateUserProfile(
        fullName: String,
        email: String,
        phone: String,
        avatarKey: String? = null,
        removeImage: Boolean = false
    ): Boolean {
        val userId = getCurrentUserId() ?: return false

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

    suspend fun refreshMenuWithToken(token: String) {
        val resp = menu("Bearer $token")
        DependencyProvider.updateMenu(resp.menu)
    }

}
