package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CashService {

    // --- Caja ---

    @POST("api/cashbox/open")
    suspend fun openCashbox(
        @Body body: OpenCashRequest
    ): Response<OpenCashResponse>

    @POST("api/cashbox/close")
    suspend fun closeCashbox(
        @Body body: CloseCashRequest
    ): Response<CloseCashResponse>

    // --- Manual Movements ---

    @POST("api/cashbox/movements")
    suspend fun createMovement(
        @Body body: CreateMovementRequest
    ): Response<CreateMovementResponse>

    // --- Consultas de sesión ---

    @GET("api/cashbox/current/{storeId}")
    suspend fun getCurrent(
        @Path("storeId") storeId: Int
    ): Response<CurrentCashResponse>

    @GET("api/cashbox/sessions")
    suspend fun listSessions(
        @Query("store_id") storeId: Int,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<SessionsListResponse>

    @GET("api/cashbox/sessions/{id}")
    suspend fun getSession(
        @Path("id") sessionId: Int
    ): Response<SessionDetailResponse>

    @GET("api/cashbox/sessions/{id}/movements")
    suspend fun getSessionMovements(
        @Path("id") sessionId: Int
    ): Response<MovementsResponse>
}

// --- Proveedor simple de Retrofit (sin Hilt) ---
object CashApi {
    // Cambia esto por tu base real. Para emulador Android hacia localhost usa 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val service: CashService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CashService::class.java)
    }
}
