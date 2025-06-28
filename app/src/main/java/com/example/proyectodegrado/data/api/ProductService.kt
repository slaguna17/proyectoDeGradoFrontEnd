package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.*

interface ProductService {
    @GET("/api/products")
    suspend fun getAllProducts(): List<Product>

    @GET("/api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @POST("/api/products/createProduct")
    suspend fun createProduct(@Body request: ProductRequest): Response<ProductResponse>

    // Endpoint corregido según Postman
    @PUT("/api/products/updateProduct/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body request: ProductRequest): Response<ProductResponse>

    // Endpoint corregido según Postman
    @DELETE("/api/products/deleteProduct/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<ProductResponse>

    @GET("/api/products/categories/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: Int): List<Product>

    @GET("/api/products/stores/{storeId}")
    suspend fun getProductsByStore(@Path("storeId") storeId: Int): List<Product>
}