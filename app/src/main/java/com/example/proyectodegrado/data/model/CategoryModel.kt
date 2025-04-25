package com.example.proyectodegrado.data.model

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val image: String
)

data class CategoryRequest(
    val name: String,
    val description: String,
    val image: String
)

data class CategoryResponse(
    val message: String, // Adjust based on your backend response
    val categoryId: Int? = null // Example of returning created user ID
)

data class CreateCategoryFormState(
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null // URL de la imagen de categoría
)