package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.CategoryRequest
import com.example.proyectodegrado.data.model.CategoryResponse
import com.example.proyectodegrado.data.model.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {

    //Get all Categories
    @GET("/api/categories")
    suspend fun getAllCategories():List<Category>

    //Get specific Category
    @GET("/api/categories/{id}")
    suspend fun getCategory(@Path("id") categoryId: Int): Category

    //Create new Category
    @POST("/api/categories/createCategory")
    suspend fun createCategory(@Body request: CategoryRequest): Response<CategoryResponse>

    //Update Category
    @PUT("/api/categories/updateCategory/{id}")
    suspend fun updateCategory(@Path("id")categoryId: Int, @Body request: CategoryRequest): Response<CategoryResponse>

    //Delete Category
    @DELETE("/api/categories/deleteCategory/{id}")
    suspend fun deleteCategory(@Path("id")categoryId: Int): Response<CategoryResponse>

    //Get products of a category
    @GET("/api/products/categories/{id}")
    suspend fun getProductsForCategory(@Path("id") categoryId: Int): List<Product>

}