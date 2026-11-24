package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface CashService {
    @POST("api/cashbox/open")
    suspend fun openCashbox(@Body body: OpenCashRequest): Response<OpenCashResponse>

    @POST("api/cashbox/close")
    suspend fun closeCashbox(@Body body: CloseCashRequest): Response<CloseCashResponse>

    // --- Manual Movements ---
    @POST("api/cashbox/movements")
    suspend fun createMovement(@Body body: CreateMovementRequest): Response<CreateMovementResponse>

    // --- Cash Session ---
    @GET("api/cashbox/current/{storeId}")
    suspend fun getCurrent(@Path("storeId") storeId: Int): Response<CurrentCashResponse>

    @GET("api/cashbox/sessions")
    suspend fun listSessions(
        @Query("store_id") storeId: Int,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<SessionsListResponse>

    @GET("api/cashbox/sessions/{id}")
    suspend fun getSession(@Path("id") sessionId: Int): Response<SessionDetailResponse>

    @GET("api/cashbox/sessions/{id}/movements")
    suspend fun getSessionMovements(@Path("id") sessionId: Int): Response<MovementsResponse>
}
