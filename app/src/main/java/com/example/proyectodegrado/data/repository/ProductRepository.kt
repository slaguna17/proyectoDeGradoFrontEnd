package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.StoreProductRequest
import com.example.proyectodegrado.data.model.StoreProductUpsertResponse
import retrofit2.HttpException
import java.io.IOException

class ProductRepository(private val productService: ProductService) {

    private inline fun <reified T> responseHandler(
        block: () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val resp = block()

            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else if (T::class == Unit::class) {
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(Unit as T)
                } else {
                    ApiResult.Error("Empty response body", resp.code())
                }
            } else {
                ApiResult.Error(resp.errorBody()?.string() ?: "Unknown error", resp.code())
            }
        } catch (e: HttpException) {
            ApiResult.Error(e.message(), e.code())
        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResult.Error("Unexpected error: ${e.message}")
        }
    }

    suspend fun getAllProducts(): ApiResult<List<Product>> {
        return responseHandler { productService.getAllProducts() }
    }

    suspend fun getProductById(id: Int): ApiResult<Product> {
        return responseHandler { productService.getProductById(id) }
    }

    suspend fun createProduct(request: ProductRequest): ApiResult<Product> {
        return responseHandler { productService.createProduct(request) }
    }

    suspend fun updateProduct(id: Int, request: ProductRequest): ApiResult<Unit> {
        return responseHandler { productService.updateProduct(id, request) }
    }

    suspend fun deleteProduct(id: Int): ApiResult<Unit> {
        return responseHandler { productService.deleteProduct(id) }
    }

    suspend fun getProductsByCategory(categoryId: Int): ApiResult<List<Product>> {
        return responseHandler { productService.getProductsByCategory(categoryId) }
    }

    suspend fun getProductsByStore(storeId: Int): ApiResult<List<Product>> {
        return responseHandler { productService.getProductsByStore(storeId) }
    }

    suspend fun addProductToStore(
        productId: Int,
        storeId: Int,
        stock: Int,
        expirationDate: String? = null
    ): ApiResult<StoreProductUpsertResponse> {
        val request = StoreProductRequest(
            storeId = storeId,
            productId = productId,
            stock = stock,
            expirationDate = expirationDate
        )
        return responseHandler { productService.addProductToStore(request) }
    }

    suspend fun removeProductFromStore(productId: Int, storeId: Int): ApiResult<Unit> {
        return responseHandler { productService.removeProductFromStore(storeId, productId) }
    }

    suspend fun getProductsByCategoryAndStore(categoryId: Int, storeId: Int): ApiResult<List<Product>> {
        return responseHandler { productService.getProductsByCategoryAndStore(categoryId, storeId) }
    }
}