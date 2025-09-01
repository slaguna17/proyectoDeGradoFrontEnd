package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.model.*
import retrofit2.Response

class CategoryRepository(private val categoryService: CategoryService){

    suspend fun getAllCategories(): List<Category> =
        categoryService.getAllCategories()

    suspend fun getCategory(categoryId: Int): Category =
        categoryService.getCategory(categoryId)

    suspend fun createCategory(name: String, description: String?, imageKey: String?): Response<Category> {
        val req = CategoryRequest(name = name, description = description, imageKey = imageKey)
        return categoryService.createCategory(req)
    }

    suspend fun updateCategory(categoryId: Int, request: CategoryRequest): Response<Unit> {
        return categoryService.updateCategory(categoryId, request)
    }

    suspend fun deleteCategory(categoryId: Int): Response<Unit> =
        categoryService.deleteCategory(categoryId)

    suspend fun getProductsForCategory(categoryId: Int): List<Product> =
        categoryService.getProductsForCategory(categoryId)
}
