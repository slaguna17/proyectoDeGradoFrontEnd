package com.example.proyectodegrado.data.model

data class MenuItemDTO(
    val id: String,
    val label: String,
    val route: String,
    val icon: String
)

data class MenuResponse(
    val isAdmin: Boolean,
    val permits: List<Permit>,
    val menu: List<MenuItemDTO>
)

data class MeResponse(
    val user: UserSummary,
    val roles: List<Role>,
    val permits: List<Permit>,
    val isAdmin: Boolean,
    val menu: List<MenuItemDTO>
)

data class UserSummary(
    val id: Int,
    val username: String?,
    val full_name: String,
    val email: String
)
