package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class Store(
    val id: Int,
    val name: String,
    val address: String,
    val city: String,
    val logo: String,
    val history: String,
    val phone: String
    // val socials: List<String>
)

data class StoreRequest(
    val name: String,
    val address: String,
    val city: String,
    val logo: String,
    val history: String,
    val phone: String
    // val socials: List<String>
)

data class StoreResponse(
    val message: String,
    @SerializedName("store_id")
    val storeId: Int? = null
)
