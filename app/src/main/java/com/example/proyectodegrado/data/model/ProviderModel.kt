package com.example.proyectodegrado.data.model

data class Provider(
    val id: Int,
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    val contact_person_name: String,
    val notes: String,
)

data class ProviderRequest(
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    val contact_person_name: String,
    val notes: String,
)

data class ProviderResponse(
    val message: String,
    val storeId: Int? = null
)