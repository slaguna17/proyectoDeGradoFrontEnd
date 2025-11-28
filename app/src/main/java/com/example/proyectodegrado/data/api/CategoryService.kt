package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.Product
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {
    @GET("/api/categories?signed=true")
    suspend fun getAllCategories(): List<Category>

    @GET("/api/categories/{id}?signed=true")
    suspend fun getCategory(@Path("id") categoryId: Int): Category

    @POST("/api/categories")
    suspend fun createCategory(@Body request: CategoryRequest): Response<Category>

    @PUT("/api/categories/{id}")
    suspend fun updateCategory(@Path("id") categoryId: Int, @Body request: CategoryRequest): Response<Unit>

    @DELETE("/api/categories/{id}")
    suspend fun deleteCategory(@Path("id") categoryId: Int): Response<Unit>

    @GET("/api/products/categories/{id}")
    suspend fun getProductsForCategory(@Path("id") categoryId: Int): List<Product>
}
