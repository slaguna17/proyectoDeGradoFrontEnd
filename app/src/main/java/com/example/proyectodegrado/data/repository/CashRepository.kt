package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.CashApi
import com.example.proyectodegrado.data.api.CashService
import com.example.proyectodegrado.data.model.*
import retrofit2.HttpException
import java.io.IOException

class CashRepository(
    private val api: CashService = CashApi.service
) {

    // Helpers
    private inline fun <reified T> ResponseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val resp = block()
            if (resp.isSuccessful && resp.body() != null) {
                ApiResult.Success(resp.body()!!)
            } else {
                ApiResult.Error(resp.errorBody()?.string() ?: "Unknown error", resp.code())
            }
        } catch (e: HttpException) {
            ApiResult.Error(e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.message}")
        }
    }

    // --- Caja ---
    suspend fun openCashbox(storeId: Int, openingAmount: Double): ApiResult<CashboxSession> {
        return when (val res = ResponseHandler { api.openCashbox(OpenCashboxRequest(storeId, openingAmount)) }) {
            is ApiResult.Success -> ApiResult.Success(res.data.cashbox)
            is ApiResult.Error -> res
        }
    }

    suspend fun closeCashbox(
        storeId: Int,
        userId: Int,
        date: String,
        closingAmount: Double? = null,
        cashCount: List<CashCountItem>? = null
    ): ApiResult<CashboxSession> {
        val req = CloseCashboxRequest(storeId, userId, date, closingAmount, cashCount)
        return when (val res = ResponseHandler { api.closeCashbox(req) }) {
            is ApiResult.Success -> ApiResult.Success(res.data.summary)
            is ApiResult.Error -> res
        }
    }

    // --- Movimientos ---
    suspend fun createMovement(
        storeId: Int,
        userId: Int,
        direction: String,
        amount: Double,
        category: String?,
        notes: String?,
        date: String?
    ): ApiResult<CashMovement> {
        val req = CreateMovementRequest(storeId, userId, direction, amount, category, notes, date)
        return when (val res = ResponseHandler { api.createMovement(req) }) {
            is ApiResult.Success -> ApiResult.Success(res.data.movement)
            is ApiResult.Error -> res
        }
    }

    // --- Consultas ---
    suspend fun getCurrent(storeId: Int): ApiResult<CurrentCashResponse> =
        ResponseHandler { api.getCurrent(storeId) }

    suspend fun listSessions(storeId: Int, from: String? = null, to: String? = null):
            ApiResult<SessionsListResponse> = ResponseHandler { api.listSessions(storeId, from, to) }

    suspend fun getSession(sessionId: Int): ApiResult<SessionDetailResponse> =
        ResponseHandler { api.getSession(sessionId) }

    suspend fun getSessionMovements(sessionId: Int): ApiResult<MovementsResponse> =
        ResponseHandler { api.getSessionMovements(sessionId) }
}
