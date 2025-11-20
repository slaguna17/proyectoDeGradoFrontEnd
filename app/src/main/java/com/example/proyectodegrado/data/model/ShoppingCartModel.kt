package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class ShoppingCart(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("customer_name") val customerName: String?,
    @SerializedName("total_estimated") val totalEstimated: Double,
    val status: String,
    @SerializedName("created_at") val createdAt: String,
    // Aquí usamos la nueva clase ReservedCartItem
    var items: List<ReservedCartItem> = emptyList()
)

// Clase renombrada para evitar conflicto con el CartItem de ventas normales
data class ReservedCartItem(
    val id: Int?,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("unit_price") val unitPrice: Double,
    var quantity: Int
)

data class FinalizeSaleRequest(
    val userId: Int,
    val paymentMethod: String
)

data class UpdateCartRequest(
    val items: List<CartItemUpdate>
)

data class CartItemUpdate(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Double
)