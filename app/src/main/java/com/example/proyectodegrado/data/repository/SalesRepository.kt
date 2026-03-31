package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.SalesService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.SaleRequest
import com.example.proyectodegrado.data.model.SaleResponse
import retrofit2.HttpException
import java.io.IOException

class SalesRepository(private val salesService: SalesService) {

    private inline fun <reified T> responseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val response = block()

            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                ApiResult.Error(
                    message = errorBody ?: "Unknown server error",
                    code = response.code()
                )
            }
        } catch (e: HttpException) {
            ApiResult.Error(
                message = e.message(),
                code = e.code()
            )
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