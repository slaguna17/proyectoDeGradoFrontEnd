package com.example.proyectodegrado.data.model

data class PurchaseRequest(
    val user_id: Int,
    val store_id: Int,
    val payment_method: String,
    val notes: String?,
    val products: List<PurchaseProductDetail>
)

data class PurchaseProductDetail(
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double
)

data class PurchaseResponse(
    val message: String,
    val purchase: Purchase
)

data class Purchase(
    val id: Int,
    val total: Double
)