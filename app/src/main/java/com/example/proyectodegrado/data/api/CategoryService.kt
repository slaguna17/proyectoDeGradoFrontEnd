package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.CategoryWriteResponse
import com.example.proyectodegrado.data.model.Product
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {

    // Get all Categories (con ?signed=true para obtener image_url)
    @GET("/api/categories?signed=true")
    suspend fun getAllCategories(): List<Category>

    // Get specific Category (también con ?signed=true)
    @GET("/api/categories/{id}?signed=true")
    suspend fun getCategory(@Path("id") categoryId: Int): Category

    // Create new Category (envía image_key)
    @POST("/api/categories")
    suspend fun createCategory(@Body request: CategoryRequest): Response<CategoryWriteResponse>

    // Update Category
    @PUT("/api/categories/{id}")
    suspend fun updateCategory(@Path("id") categoryId: Int, @Body request: CategoryRequest): Response<CategoryWriteResponse>

    // Delete Category
    @DELETE("/api/categories/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: Int): Response<Unit>

    // Get products of a category (puedes añadir ?signed=true a tu backend de productos también)
    @GET("/api/products/categories/{id}")
    suspend fun getProductsForCategory(@Path("id") categoryId: Int): List<Product>
}
