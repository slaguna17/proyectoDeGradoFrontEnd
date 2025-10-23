package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

// Objeto para enviar al crear una venta
data class SaleRequest(
    val user_id: Int,
    val store_id: Int,
    val payment_method: String,
    val notes: String?,
    val products: List<SaleProductDetail>
)

// Detalle de cada producto dentro de la venta
data class SaleProductDetail(
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double
)

// Respuesta esperada del servidor tras crear la venta
data class SaleResponse(
    val message: String,
    val sale: Sale // Un objeto que representa la venta creada
)

// Modelo para una venta individual (puedes completarlo más si lo necesitas)
data class Sale(
    val id: Int,
    val total: Double
)