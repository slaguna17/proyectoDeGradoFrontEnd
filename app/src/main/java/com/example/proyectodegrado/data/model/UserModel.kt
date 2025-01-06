package com.example.proyectodegrado.data.model

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