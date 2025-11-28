package com.example.proyectodegrado.data.model

data class SaleRequest(
    val user_id: Int,
    val store_id: Int,
    val payment_method: String,
    val notes: String?,
    val products: List<SaleProductDetail>
)

data class SaleProductDetail(
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double
)

data class SaleResponse(
    val message: String,
    val sale: Sale
)

data class Sale(
    val id: Int,
    val total: Double
)