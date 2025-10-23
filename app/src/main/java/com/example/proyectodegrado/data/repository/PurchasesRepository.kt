package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.PurchasesService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.PurchaseRequest
import com.example.proyectodegrado.data.model.PurchaseResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class PurchasesRepository (private val purchasesService: PurchasesService) {
    private inline fun <reified T> responseHandler(
        block: () -> Response<T>
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

    suspend fun createPurchase(purchaseRequest: PurchaseRequest): ApiResult<PurchaseResponse> {
        return responseHandler { purchasesService.createPurchase(purchaseRequest) }
    }
}