package com.example.proyectodegrado.data.model

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val image: String?
)

data class CategoryRequest(
    val name: String,
    val description: String,
    val image: String?
)

data class CategoryResponse(
    val message: String,
    val categoryId: Int? = null
)

data class CreateCategoryFormState(
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null
)