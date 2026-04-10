package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderLinkedProduct
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.ProviderResponse
import com.example.proyectodegrado.data.model.SyncProviderProductsRequest
import com.example.proyectodegrado.data.model.SyncProviderProductsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface ProviderService {

    @GET("/api/providers")
    suspend fun getAllProviders(): Response<List<Provider>>

    @GET("/api/providers/{id}")
    suspend fun getProvider(@Path("id") providerId: Int): Response<Provider>

    @POST("/api/providers/createProvider")
    suspend fun createProvider(@Body request: ProviderRequest): Response<ProviderResponse>

    @PUT("/api/providers/updateProvider/{id}")
    suspend fun updateProvider(
        @Path("id") providerId: Int,
        @Body request: ProviderRequest
    ): Response<ProviderResponse>

    @DELETE("/api/providers/deleteProvider/{id}")
    suspend fun deleteProvider(@Path("id") providerId: Int): Response<ProviderResponse>

    @GET("/api/providers/{id}/products")
    suspend fun getProductsByProvider(
        @Path("id") providerId: Int
    ): Response<List<ProviderLinkedProduct>>

    @PUT("/api/providers/{id}/products")
    suspend fun syncProviderProducts(
        @Path("id") providerId: Int,
        @Body request: SyncProviderProductsRequest
    ): Response<SyncProviderProductsResponse>
}