package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.ProviderResponse
import retrofit2.Response

class ProviderRepository (private val providerService: ProviderService) {
    suspend fun getAllProviders(): List<Provider>{
        return providerService.getAllProviders()
    }

    suspend fun getProvider(providerId: Int): Provider{
        return providerService.getProvider(providerId)
    }

    suspend fun createProvider(request: ProviderRequest):Response<ProviderResponse>{
        return providerService.createProvider(request)
    }

    suspend fun updateProvider(providerId: Int, request: ProviderRequest): Response<ProviderResponse> {
        return providerService.updateProvider(providerId,request)
    }

    suspend fun deleteProvider(providerid: Int): Response<ProviderResponse>{
        return providerService.deleteProvider(providerid)
    }

}
