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
    // Login
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return userService.login(LoginRequest(email, password))
    }

    // Register
    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return userService.registerUser(request)
    }

    // Get all users
    suspend fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    // Get user by ID
    suspend fun getUserById(userId: Int): User {
        return userService.getUser(userId)
    }

    // Get all roles
    suspend fun getRoles(): List<Role> {
        return userService.getRoles()
    }

    // Forgot Password
    suspend fun forgotPassword(email: String): Response<ForgotPasswordResponse> {
        return userService.forgotPassword(ForgotPasswordRequest(email))
    }

    // Reset Password
    suspend fun resetPassword(token: String, newPassword: String): Response<ResetPasswordResponse> {
        return userService.resetPassword(ResetPasswordRequest(token, newPassword))
    }

    // Get current user (by id from preferences)
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId()
        return if (userId != null) {
            userService.getUser(userId)
        } else {
            null
        }
    }

    // Update user profile (nombre, email, teléfono)
    suspend fun updateUserProfile(fullName: String, email: String, phone: String): Boolean {
        val userId = getCurrentUserId()
        if (userId == null) return false
        val req = mapOf(
            "full_name" to fullName,
            "email" to email,
            "phone" to phone
        )
        return try {
            val response = userService.updateUser(userId, req)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Obtener el ID del usuario actual guardado en preferencias
    private fun getCurrentUserId(): Int? {
        return AppPreferences(context).getUserId()?.toIntOrNull()
    }
}
