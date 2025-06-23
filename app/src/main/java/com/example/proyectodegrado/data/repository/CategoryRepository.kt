package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.CategoryResponse
import com.example.proyectodegrado.data.model.Product
import retrofit2.Response

class CategoryRepository (private val categoryService: CategoryService){

    //Get all Categories
    suspend fun getAllCategories(): List<Category>{
        return categoryService.getAllCategories()
    }

    //Get specific Category
    suspend fun getCategory(categoryId: Int): Category {
        return categoryService.getCategory(categoryId)
    }

    //Create new Category
    suspend fun createCategory(request: CategoryRequest): Response<CategoryResponse> {
        return categoryService.createCategory(request)
    }

    //Update Category
    suspend fun updateCategory(categoryId: Int, request: CategoryRequest): Response<CategoryResponse> {
        return categoryService.updateCategory(categoryId, request)
    }

    //Delete Category
    suspend fun deleteCategory(categoryId: Int): Response<CategoryResponse> {
        return categoryService.deleteCategory(categoryId)
    }

    //Get products of a category
    suspend fun getProductsForCategory(categoryId: Int): List<Product>{
        return categoryService.getProductsForCategory(categoryId)
    }
}