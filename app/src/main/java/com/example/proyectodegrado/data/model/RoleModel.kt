package com.example.proyectodegrado.data.model

data class Role(
    val id: Int,
    val name: String,
    val description: String,
    var isAdmin: Boolean
)

data class RoleRequest(
    val name: String,
    val description: String,
    val isAdmin: Boolean
)

data class CreateRoleResponse(
    val message: String,
    val role: Role
)

data class GenericRoleResponse(
    val message: String
)

data class AssignPermitsRequest(
    val permitIds: List<Int>
)