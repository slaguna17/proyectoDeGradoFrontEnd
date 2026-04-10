package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderLinkedProduct
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.ProviderResponse
import com.example.proyectodegrado.data.model.SyncProviderProductsRequest
import com.example.proyectodegrado.data.model.SyncProviderProductsResponse
import retrofit2.HttpException
import java.io.IOException

class ProviderRepository(
    private val providerService: ProviderService
) {

    private inline fun <reified T> responseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val response = block()

            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(
                    message = response.errorBody()?.string() ?: "Unknown error",
                    code = response.code()
                )
            }
        } catch (e: HttpException) {
            ApiResult.Error(e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun getAllProviders(): ApiResult<List<Provider>> {
        return responseHandler { providerService.getAllProviders() }
    }

    suspend fun getProvider(providerId: Int): ApiResult<Provider> {
        return responseHandler { providerService.getProvider(providerId) }
    }

    suspend fun createProvider(request: ProviderRequest): ApiResult<ProviderResponse> {
        return responseHandler { providerService.createProvider(request) }
    }

    suspend fun updateProvider(providerId: Int, request: ProviderRequest): ApiResult<ProviderResponse> {
        return responseHandler { providerService.updateProvider(providerId, request) }
    }

    suspend fun deleteProvider(providerId: Int): ApiResult<ProviderResponse> {
        return responseHandler { providerService.deleteProvider(providerId) }
    }

    suspend fun getProductsByProvider(providerId: Int): ApiResult<List<ProviderLinkedProduct>> {
        return responseHandler { providerService.getProductsByProvider(providerId) }
    }

    suspend fun syncProviderProducts(
        providerId: Int,
        productIds: List<Int>
    ): ApiResult<SyncProviderProductsResponse> {
        return responseHandler {
            providerService.syncProviderProducts(
                providerId,
                SyncProviderProductsRequest(productIds = productIds)
            )
        }
    }
}