package com.example.proyectodegrado.data.model

import java.util.Calendar

val currentTime = Calendar.getInstance().getTime();

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val full_name: String,
    val date_of_birth: String,
    val phone: String,
    val status: String,
    val last_access: String,
    val avatar: String
)
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val full_name: String,
    val date_of_birth: String,
    val phone: String,
    val status: String = "active", // Default value
    val last_access: String = currentTime.toString(),
    val avatar: String? = null,
    val roleId: Int
)

data class RegisterResponse(
    val message: String, // Adjust based on your backend response
    val userId: Int? = null // Example of returning created user ID
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)
