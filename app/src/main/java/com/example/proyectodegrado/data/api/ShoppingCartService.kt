package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.FinalizeSaleRequest
import com.example.proyectodegrado.data.model.ShoppingCart
import com.example.proyectodegrado.data.model.UpdateCartRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShoppingCartService {

    @GET("api/shoppingCart/store/{storeId}")
    suspend fun getCartsByStore(@Path("storeId") storeId: Int): Response<List<ShoppingCart>>

    @PUT("api/shoppingCart/{cartId}")
    suspend fun updateCart(
        @Path("cartId") cartId: Int,
        @Body request: UpdateCartRequest
    ): Response<Unit>

    @POST("api/shoppingCart/{cartId}/finalize")
    suspend fun finalizeCart(
        @Path("cartId") cartId: Int,
        @Body request: FinalizeSaleRequest
    ): Response<Unit>

    @DELETE("api/shoppingCart/{cartId}")
    suspend fun deleteCart(@Path("cartId") cartId: Int): Response<Unit>
}