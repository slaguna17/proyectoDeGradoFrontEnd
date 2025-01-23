package com.example.proyectodegrado.data.model

data class Product(
    val id: Int,
    val SKU: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: String,
    val category_id: Int
)

data class ProductRequest(
    val SKU: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: String,
    val category_id: Int
)

data class ProductResponse(
    val message: String, // Adjust based on your backend response
    val productId: Int? = null // Example of returning created user ID
)