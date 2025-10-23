package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.PurchaseRequest
import com.example.proyectodegrado.data.model.PurchaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchasesService {
    @POST("api/purchase/createPurchase")
    suspend fun createPurchase(@Body purchaseRequest: PurchaseRequest): Response<PurchaseResponse>
}