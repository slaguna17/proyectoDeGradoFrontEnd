package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.SaleRequest
import com.example.proyectodegrado.data.model.SaleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesService {
    @POST("api/sales/createSale")
    suspend fun createSale(@Body saleRequest: SaleRequest): Response<SaleResponse>
}