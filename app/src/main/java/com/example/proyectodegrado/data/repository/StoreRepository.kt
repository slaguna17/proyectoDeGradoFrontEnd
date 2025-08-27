package com.example.proyectodegrado.data.repository
import com.example.proyectodegrado.data.api.StoreService
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.StoreResponse
import retrofit2.Response

class StoreRepository (private val storeService: StoreService) {
    suspend fun getAllStores(): List<Store> = storeService.getAllStores()
    suspend fun getStore(storeId: Int): Store = storeService.getStore(storeId)
    suspend fun createStore(request: StoreRequest): Response<StoreResponse> = storeService.createStore(request)
    suspend fun updateStore(storeId: Int, request: StoreRequest): Response<StoreResponse> = storeService.updateStore(storeId, request)
    suspend fun deleteStore(storeId: Int): Response<StoreResponse> = storeService.deleteStore(storeId)
}
