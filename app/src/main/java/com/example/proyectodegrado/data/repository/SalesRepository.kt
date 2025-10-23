package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.SalesService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.SaleRequest
import com.example.proyectodegrado.data.model.SaleResponse
import retrofit2.HttpException
import java.io.IOException

class SalesRepository(private val salesService: SalesService) {

    // Helper para manejar las respuestas de la API de forma consistente
    private inline fun <reified T> responseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val resp = block()
            if (resp.isSuccessful && resp.body() != null) {
                ApiResult.Success(resp.body()!!)
            } else {
                ApiResult.Error(resp.errorBody()?.string() ?: "Error desconocido", resp.code())
            }
        } catch (e: HttpException) {
            ApiResult.Error(e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Error inesperado: ${e.message}")
        }
    }

    suspend fun createSale(saleRequest: SaleRequest): ApiResult<SaleResponse> {
        return responseHandler { salesService.createSale(saleRequest) }
    }
}