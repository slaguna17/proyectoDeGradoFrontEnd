package com.example.proyectodegrado.data.api
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.ProviderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProviderService {
    //Get all Providers, GET
    @GET("/api/providers")
    suspend fun getAllProviders():List<Provider>

    //Get specific provider
    @GET("/api/providers/{id}")
    suspend fun getProvider(@Path("id") providerId: Int): Provider

    //Create new provider
    @POST("/api/providers/createProvider")
    suspend fun createProvider(@Body request: ProviderRequest): Response<ProviderResponse>

    //Update provider
    @PUT("/api/providers/updateProvider/{id}")
    suspend fun updateProvider(@Path("id")providerId: Int, @Body request: ProviderRequest): Response<ProviderResponse>

    //Delete provider
    @DELETE("/api/providers/deleteProvider/{id}")
    suspend fun deleteProvider(@Path("id")providerId: Int): Response<ProviderResponse>

}