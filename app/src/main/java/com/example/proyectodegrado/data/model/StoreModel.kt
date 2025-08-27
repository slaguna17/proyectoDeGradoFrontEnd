package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Store(
    val id: Int,
    val name: String,
    val address: String,
    val city: String,
    val logo: String?,
    @SerializedName("logo_url")
    val logoUrl: String?,
    val history: String,
    val phone: String
)

data class StoreRequest(
    val name: String,
    val address: String,
    val city: String,
    @SerializedName("logo_key")
    val logoKey: String? = null,
    val logo: String? = null,
    val history: String,
    val phone: String
)

data class StoreResponse(
    val message: String,
    @SerializedName("store_id")
    val storeId: Int? = null
)
