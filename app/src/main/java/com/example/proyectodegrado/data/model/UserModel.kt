package com.example.proyectodegrado.data.model

import java.util.Calendar
import java.util.Date

val currentTime: Date = Calendar.getInstance().time;

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: String,
    val phone: String,
    val status: String,
    val lastAccess: String,
    val avatar: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val dateOfBirth: String,
    val phone: String,
    val status: String = "active",
    val lastAccess: String = currentTime.toString(),
    val avatar: String? = null,
    val roleId: Int
)

data class RegisterResponse(
    val message: String,
    val userId: Int? = null
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String)

data class ResetPasswordRequest(val token: String, val newPassword: String)
data class ResetPasswordResponse(val message: String)
