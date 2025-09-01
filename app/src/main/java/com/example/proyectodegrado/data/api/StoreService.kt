package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.StoreResponse
import retrofit2.Response
import retrofit2.http.*

interface StoreService {
    // Get all Stores
    @GET("/api/stores")
    suspend fun getAllStores(@Query("signed") signed: Boolean = true): List<Store>

    // Get specific Store
    @GET("/api/stores/{id}")
    suspend fun getStore(@Path("id") storeId: Int, @Query("signed") signed: Boolean = true): Store

    // Create (legacy, backend ya lo tiene)
    @POST("/api/stores/createStore")
    suspend fun createStore(@Body request: StoreRequest): Response<Store>

    // Update (legacy)
    @PUT("/api/stores/updateStore/{id}")
    suspend fun updateStore(@Path("id") storeId: Int, @Body request: StoreRequest): Response<Unit>

    // Delete (legacy)
    @DELETE("/api/stores/deleteStore/{id}")
    suspend fun deleteStore(@Path("id") storeId: Int): Response<Unit>
}
