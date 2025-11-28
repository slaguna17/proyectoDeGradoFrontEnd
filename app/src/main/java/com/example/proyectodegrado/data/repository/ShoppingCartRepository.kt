package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ShoppingCartService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.CartItemUpdate
import com.example.proyectodegrado.data.model.FinalizeSaleRequest
import com.example.proyectodegrado.data.model.ReservedCartItem
import com.example.proyectodegrado.data.model.ShoppingCart
import com.example.proyectodegrado.data.model.UpdateCartRequest
import retrofit2.HttpException
import java.io.IOException

class ShoppingCartRepository(private val service: ShoppingCartService) {

    // Helper
    private inline fun <reified T> responseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val resp = block()
            if (resp.isSuccessful) {
                if (T::class == Unit::class) {
                    ApiResult.Success(Unit as T)
                } else if (resp.body() != null) {
                    ApiResult.Success(resp.body()!!)
                } else {
                    if (T::class == Void::class) ApiResult.Success(null as T)
                    else ApiResult.Error("Respuesta vacía del servidor", resp.code())
                }
            } else {
                ApiResult.Error(resp.errorBody()?.string() ?: "Error desconocido", resp.code())
            }
        } catch (e: HttpException) {
            ApiResult.Error(e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Error de red: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Error inesperado: ${e.message}")
        }
    }

    suspend fun getCartsByStore(storeId: Int): ApiResult<List<ShoppingCart>> {
        return responseHandler { service.getCartsByStore(storeId) }
    }

    suspend fun updateCart(cartId: Int, items: List<ReservedCartItem>): ApiResult<Unit> {
        val updateItems = items.map {
            CartItemUpdate(it.productId, it.quantity, it.unitPrice)
        }
        return responseHandler { service.updateCart(cartId, UpdateCartRequest(updateItems)) }
    }

    suspend fun finalizeCart(cartId: Int, userId: Int, paymentMethod: String): ApiResult<Unit> {
        return responseHandler { service.finalizeCart(cartId, FinalizeSaleRequest(userId, paymentMethod)) }
    }

    suspend fun deleteCart(cartId: Int): ApiResult<Unit> {
        return responseHandler { service.deleteCart(cartId) }
    }
}