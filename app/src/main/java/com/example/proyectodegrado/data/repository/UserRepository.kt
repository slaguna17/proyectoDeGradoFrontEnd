package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.UserService
import com.example.proyectodegrado.data.model.ForgotPasswordRequest
import com.example.proyectodegrado.data.model.ForgotPasswordResponse
import com.example.proyectodegrado.data.model.LoginRequest
import com.example.proyectodegrado.data.model.LoginResponse
import com.example.proyectodegrado.data.model.RegisterRequest
import com.example.proyectodegrado.data.model.RegisterResponse
import com.example.proyectodegrado.data.model.ResetPasswordRequest
import com.example.proyectodegrado.data.model.ResetPasswordResponse
import com.example.proyectodegrado.data.model.Role
import com.example.proyectodegrado.data.model.User
import retrofit2.Response

class UserRepository (private val userService: UserService) {
    //Login
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

    //Forgot Password
    suspend fun forgotPassword(email: String): Response<ForgotPasswordResponse> {
        return userService.forgotPassword(ForgotPasswordRequest(email))
    }

    //Reset Password
    suspend fun resetPassword(token: String, newPassword: String): Response<ResetPasswordResponse> {
        return userService.resetPassword(ResetPasswordRequest(token, newPassword))
    }

}
