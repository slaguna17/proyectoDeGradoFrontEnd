package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date

val currentTime: Date = Calendar.getInstance().time

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    val phone: String,
    val status: String,
    @SerializedName("last_access")
    val lastAccess: String,
    val avatar: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    val phone: String,
    val status: String = "active",
    @SerializedName("last_access")
    val lastAccess: String = currentTime.toString(),
    val avatar: String? = null,
    @SerializedName("role_id")
    val roleId: Int
)

data class RegisterResponse(
    val message: String,
    @SerializedName("user_id")
    val userId: Int? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String)

data class ResetPasswordRequest(
    val token: String,
    @SerializedName("new_password")
    val newPassword: String
)
data class ResetPasswordResponse(val message: String)
