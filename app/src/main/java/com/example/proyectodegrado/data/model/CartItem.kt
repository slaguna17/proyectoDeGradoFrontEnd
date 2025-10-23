package com.example.proyectodegrado.data.model

data class CartItem(
    val productId: Int,
    val name: String,
    val unitPrice: Double,
    var quantity: Int
)