package com.example.proyectodegrado.data.api

import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.StoreResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StoreService {
    //Get all Stores, GET
    @GET("/api/stores")
    suspend fun getAllStores():List<Store>

    //Get specific Store
    @GET("/api/stores/{id}")
    suspend fun getStore(@Path("id") storeId: Int): Store

    //Create new Store
    @POST("/api/stores/createStore")
    suspend fun createStore(@Body request: StoreRequest): Response<StoreResponse>

    //Update Store
    @PUT("/api/stores/updateStore/{id}")
    suspend fun updateStore(@Path("id")storeId: Int, @Body request: StoreRequest): Response<StoreResponse>

    //Delete Store
    @DELETE("/api/stores/deleteStore/{id}")
    suspend fun deleteStore(@Path("id")storeId: Int): Response<StoreResponse>

}