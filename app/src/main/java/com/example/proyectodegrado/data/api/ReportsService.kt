package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.PurchasesByProviderResponse
import com.example.proyectodegrado.data.model.PurchasesDetailsResponse
import com.example.proyectodegrado.data.model.PurchasesSummaryResponse
import com.example.proyectodegrado.data.model.PurchasesTopProductsResponse
import com.example.proyectodegrado.data.model.SalesDetailsResponse
import com.example.proyectodegrado.data.model.SalesSummaryResponse
import com.example.proyectodegrado.data.model.SalesTopProductsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ReportsService {

    @GET("api/reports/sales/summary")
    suspend fun getSalesSummary(
        @QueryMap query: Map<String, String>
    ): Response<SalesSummaryResponse>

    @GET("api/reports/sales/details")
    suspend fun getSalesDetails(
        @QueryMap query: Map<String, String>
    ): Response<SalesDetailsResponse>

    @GET("api/reports/sales/top-products")
    suspend fun getSalesTopProducts(
        @QueryMap query: Map<String, String>
    ): Response<SalesTopProductsResponse>

    @GET("api/reports/purchases/summary")
    suspend fun getPurchasesSummary(
        @QueryMap query: Map<String, String>
    ): Response<PurchasesSummaryResponse>

    @GET("api/reports/purchases/details")
    suspend fun getPurchasesDetails(
        @QueryMap query: Map<String, String>
    ): Response<PurchasesDetailsResponse>

    @GET("api/reports/purchases/top-products")
    suspend fun getPurchasesTopProducts(
        @QueryMap query: Map<String, String>
    ): Response<PurchasesTopProductsResponse>

    @GET("api/reports/purchases/by-provider")
    suspend fun getPurchasesByProvider(
        @QueryMap query: Map<String, String>
    ): Response<PurchasesByProviderResponse>
}