package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.StoreProductRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductService {
    @GET("/api/products")
    suspend fun getAllProducts(@Query("signed") signed: Boolean = true): List<Product>

    @GET("/api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int, @Query("signed") signed: Boolean = true): Product

    @POST("/api/products/createProduct")
    suspend fun createProduct(@Body request: ProductRequest): Response<Product>

    @PUT("/api/products/updateProduct/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body request: ProductRequest): Response<Unit>

    @DELETE("/api/products/deleteProduct/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    @GET("/api/products/categories/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: Int, @Query("signed") signed: Boolean = true): List<Product>

    @GET("/api/products/stores/{storeId}")
    suspend fun getProductsByStore(@Path("storeId") storeId: Int, @Query("signed") signed: Boolean = true): List<Product>

    @POST("/api/products/store-products/upsert")
    suspend fun addProductToStore(@Body request: StoreProductRequest): Response<Unit>

    @DELETE("/api/products/stores/{storeId}/products/{productId}")
    suspend fun removeProductFromStore(
        @Path("storeId") storeId: Int,
        @Path("productId") productId: Int
    ): Response<Unit>

    @GET("/api/products/categories/{categoryId}/stores/{storeId}")
    suspend fun getProductsByCategoryAndStore(
        @Path("categoryId") categoryId: Int,
        @Path("storeId") storeId: Int,
        @Query("signed") signed: Boolean = true
    ): List<Product>
}
