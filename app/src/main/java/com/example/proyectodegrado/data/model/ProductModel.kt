package com.example.proyectodegrado.data.model

data class Product(
    val id: Int,
    val SKU: String,
    val name: String,
    val description: String,
    val image: String,
    val brand: String,
    val category_id: Int
)

data class ProductRequest(
    val product: ProductData,
    val store: StoreData
)

data class ProductRequest2(
    val product: Product,
    val store: StoreData
)

data class ProductData(
    val SKU: String,
    val name: String,
    val description: String,
    val image: String,
    val category_id: Int,
    val brand: String
)

data class StoreData(
    val store_id: Int,
    val stock: Int,
    val expiration_date: String
)

data class ProductResponse(
    val message: String, // Adjust based on your backend response
    val productId: Int? = null // Example of returning created user ID
)

