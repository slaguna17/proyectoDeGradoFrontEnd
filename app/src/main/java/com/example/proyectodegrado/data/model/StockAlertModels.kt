package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class StockAlertProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stock") val stock: Int,
    @SerializedName("minStock") val minStock: Int,
    @SerializedName("status") val status: String
)

data class StockAlertResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("products") val products: List<StockAlertProductDto>
)

data class StockAlertSummaryDto(
    @SerializedName("lowStockCount") val lowStockCount: Int,
    @SerializedName("outOfStockCount") val outOfStockCount: Int,
    @SerializedName("hasAlerts") val hasAlerts: Boolean
)
