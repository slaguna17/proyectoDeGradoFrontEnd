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
    @SerializedName("full_name") val fullName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    val phone: String,
    val status: String,
    @SerializedName("last_access") val lastAccess: String,
    val avatar: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    val phone: String,
    val status: String = "active",
    @SerializedName("last_access") val lastAccess: String = currentTime.toString(),
    @SerializedName("avatar_key") val avatarKey: String? = null,
    val avatar: String? = null,
    @SerializedName("role_id") val roleId: Int
)

data class RegisterResponse(
    val message: String,
    @SerializedName("user_id") val userId: Int? = null
)

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val token: String,
    val user: UserSummary,
    val roles: List<Role>,
    val permits: List<Permit>,
    val isAdmin: Boolean,
    val menu: List<MenuItemDTO>
)


data class RoleDTO(
    val id: Int,
    val name: String,
    val isAdmin: Boolean
)

data class PermitDTO(
    val id: Int,
    val name: String,
    val description: String?
)

data class UserDTO(
    val id: Int,
    val username: String?,
    @SerializedName("full_name") val fullName: String,
    val roles: List<RoleDTO>? = emptyList(),
    val isAdmin: Boolean
)

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val message: String)
data class ResetPasswordRequest(@SerializedName("token") val token: String, @SerializedName("new_password") val newPassword: String)
data class ResetPasswordResponse(val message: String)