package com.example.proyectodegrado.data.repository
import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.api.StoreService
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.ProductResponse
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.data.model.StoreResponse
import retrofit2.Response

class StoreRepository (private val storeService: StoreService) {
    //Get all Stores
    suspend fun getAllStores(): List<Store>{
        return storeService.getAllStores()
    }

    //Get specific Store
    suspend fun getStore(storeId: Int): Store{
        return storeService.getStore(storeId)
    }

    //Create new Store
    suspend fun createStore(request: StoreRequest):Response<StoreResponse>{
        return storeService.createStore(request)
    }

    //Update Store
    suspend fun updateStore(storeId: Int, request: StoreRequest): Response<StoreResponse> {
        return storeService.updateStore(storeId,request)
    }

    //Delete Store
    suspend fun deleteStore(storeId: Int): Response<StoreResponse>{
        return storeService.deleteStore(storeId)
    }

}
