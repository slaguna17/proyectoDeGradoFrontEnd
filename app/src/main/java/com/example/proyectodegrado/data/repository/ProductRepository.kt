package com.example.proyectodegrado.data.repository

import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.model.Product
import com.example.proyectodegrado.data.model.ProductRequest
import com.example.proyectodegrado.data.model.ProductResponse
import retrofit2.Response

class ProductRepository (private val productService: ProductService) {
    //Get all Products
    suspend fun getAllProducts(): List<Product>{
        return productService.getAllProducts()
    }

    //Get specific Product
    suspend fun getProduct(productId: Int): Product{
        return productService.getProduct(productId)
    }

    //Create new Product
    suspend fun createProduct(request: ProductRequest):Response<ProductResponse>{
        return productService.createProduct(request)
    }

    //Update Product
    suspend fun updateProduct(productId: Int, request: ProductRequest): Response<ProductResponse> {
        return productService.updateProduct(productId, request)
    }

    //Delete Product
    suspend fun deleteProduct(productId: Int): Response<ProductResponse>{
        return productService.deleteProduct(productId)
    }

}