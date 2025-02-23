package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.ProviderResponse
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.StoreResponse
import retrofit2.Response

class ProviderRepository (private val providerService: ProviderService) {
    //Get all Providers
    suspend fun getAllProviders(): List<Provider>{
        return providerService.getAllProviders()
    }

    //Get specific provider
    suspend fun getProvider(providerId: Int): Provider{
        return providerService.getProvider(providerId)
    }

    //Create new Provider
    suspend fun createProvider(request: ProviderRequest):Response<ProviderResponse>{
        return providerService.createProvider(request)
    }

    //Update Provider
    suspend fun updateProvider(providerId: Int, request: ProviderRequest): Response<ProviderResponse> {
        return providerService.updateProvider(providerId,request)
    }

    //Delete Provider
    suspend fun deleteProvider(providerid: Int): Response<ProviderResponse>{
        return providerService.deleteProvider(providerid)
    }

}
