package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import retrofit2.Response

class ProductRepository(private val productService: ProductService) {
    suspend fun getAllProducts(): List<Product> {
        return productService.getAllProducts()
    }

    suspend fun getProductById(id: Int): Product {
        return productService.getProductById(id)
    }

    suspend fun createProduct(request: ProductRequest): Response<Product> {
        return productService.createProduct(request)
    }

    suspend fun updateProduct(id: Int, request: ProductRequest): Response<Unit> {
        return productService.updateProduct(id, request)
    }

    suspend fun deleteProduct(id: Int): Response<Unit> {
        return productService.deleteProduct(id)
    }

    suspend fun getProductsByCategory(categoryId: Int): List<Product> {
        return productService.getProductsByCategory(categoryId)
    }

    suspend fun getProductsByStore(storeId: Int): List<Product> {
        return productService.getProductsByStore(storeId)
    }
}