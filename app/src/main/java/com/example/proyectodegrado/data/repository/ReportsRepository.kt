package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ReportsService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.PurchasesReportBundle
import com.example.proyectodegrado.data.model.SalesReportBundle
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ReportsRepository(
    private val reportsService: ReportsService
) {

    private suspend inline fun <reified T> responseHandler(
        crossinline block: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = block()

            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error(
                    message = response.errorBody()?.string() ?: "Unknown server error",
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

    private fun buildQueryMap(
        storeId: Int,
        from: String,
        to: String
    ): Map<String, String> {
        val map = mutableMapOf(
            "store_id" to storeId.toString()
        )

        if (from.isNotBlank()) map["from"] = from
        if (to.isNotBlank()) map["to"] = to

        return map
    }

    suspend fun getSalesReport(
        storeId: Int,
        from: String,
        to: String
    ): ApiResult<SalesReportBundle> {
        val query = buildQueryMap(storeId, from, to)

        val summary = when (val result = responseHandler { reportsService.getSalesSummary(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        val details = when (val result = responseHandler { reportsService.getSalesDetails(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        val topProducts = when (val result = responseHandler { reportsService.getSalesTopProducts(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        return ApiResult.Success(
            SalesReportBundle(
                summary = summary,
                details = details,
                topProducts = topProducts
            )
        )
    }

    suspend fun getPurchasesReport(
        storeId: Int,
        from: String,
        to: String
    ): ApiResult<PurchasesReportBundle> {
        val query = buildQueryMap(storeId, from, to)

        val summary = when (val result = responseHandler { reportsService.getPurchasesSummary(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        val details = when (val result = responseHandler { reportsService.getPurchasesDetails(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        val topProducts = when (val result = responseHandler { reportsService.getPurchasesTopProducts(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        val byProvider = when (val result = responseHandler { reportsService.getPurchasesByProvider(query) }) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> return result
        }

        return ApiResult.Success(
            PurchasesReportBundle(
                summary = summary,
                details = details,
                topProducts = topProducts,
                byProvider = byProvider
            )
        )
    }
}