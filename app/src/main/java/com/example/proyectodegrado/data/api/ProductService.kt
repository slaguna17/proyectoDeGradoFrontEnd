package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Category
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductService {
    //Get all Products, GET
    @GET("/api/products")
    suspend fun getAllProducts():List<Product>

    //Get specific Product
    @GET("/api/products/{id}")
    suspend fun getProduct(@Path("id") productId: Int): Product

    //Create new Product
    @POST("/api/products/createProduct")
    suspend fun createProduct(@Body request: ProductRequest): Response<ProductResponse>

    //Update Product
    @PUT("/api/products/updateProduct/{id}")
    suspend fun updateProduct(@Path("id")productId: Int, @Body request: ProductRequest):Response<ProductResponse>

    //Delete Product
    @DELETE("/api/products/deleteProduct/{id}")
    suspend fun deleteProduct(@Path("id")productId: Int):Response<ProductResponse>

}